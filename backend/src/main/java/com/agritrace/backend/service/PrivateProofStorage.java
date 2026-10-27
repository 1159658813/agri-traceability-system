package com.agritrace.backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PrivateProofStorage {
    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final String PREFIX = "PRIVATE_PROOFS/";
    private final Path root = Path.of(System.getProperty("user.home"),
            ".agri-traceability", "private-proofs").toAbsolutePath().normalize();

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() > MAX_SIZE) {
            throw new RegistrationProblem(400, "请上传不超过5MB的资质文件");
        }
        String extension = detectExtension(file);
        String name = UUID.randomUUID() + "." + extension;
        Path destination = root.resolve(name);
        try {
            Files.createDirectories(root);
            try (InputStream in = file.getInputStream();
                 OutputStream out = Files.newOutputStream(destination,
                         StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                long total = 0;
                byte[] buffer = new byte[8192];
                int count;
                while ((count = in.read(buffer)) != -1) {
                    total += count;
                    if (total > MAX_SIZE) throw new RegistrationProblem(400, "文件不能超过5MB");
                    out.write(buffer, 0, count);
                }
            }
            return PREFIX + name;
        } catch (Exception ex) {
            try { Files.deleteIfExists(destination); } catch (IOException ignored) { }
            if (ex instanceof RegistrationProblem error) throw error;
            throw new RegistrationProblem(500, "资质文件保存失败");
        }
    }

    public Path find(String storedPath) {
        if (storedPath == null || !storedPath.matches(
                "PRIVATE_PROOFS/[a-f0-9-]{36}\\.(pdf|png|jpg)")) {
            throw new RegistrationProblem(404, "资质文件不存在");
        }
        Path path = root.resolve(storedPath.substring(PREFIX.length())).normalize();
        if (!path.startsWith(root)
                || !Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
            throw new RegistrationProblem(404, "资质文件不存在");
        }
        return path;
    }

    public boolean exists(String storedPath) {
        try { find(storedPath); return true; }
        catch (RegistrationProblem ignored) { return false; }
    }

    public void remove(String storedPath) {
        try { Files.deleteIfExists(find(storedPath)); }
        catch (RegistrationProblem | IOException ignored) { }
    }

    private String detectExtension(MultipartFile file) {
        byte[] header = new byte[8];
        try (InputStream in = file.getInputStream()) {
            int length = in.read(header);
            if (length >= 5 && header[0] == '%' && header[1] == 'P'
                    && header[2] == 'D' && header[3] == 'F' && header[4] == '-') return "pdf";
            if (length >= 8 && (header[0] & 255) == 137 && header[1] == 'P'
                    && header[2] == 'N' && header[3] == 'G'
                    && (header[4] & 255) == 13 && (header[5] & 255) == 10
                    && (header[6] & 255) == 26 && (header[7] & 255) == 10) return "png";
            if (length >= 3 && (header[0] & 255) == 255
                    && (header[1] & 255) == 216 && (header[2] & 255) == 255) return "jpg";
        } catch (IOException ignored) {
            throw new RegistrationProblem(400, "无法读取上传文件");
        }
        throw new RegistrationProblem(400, "资质文件仅支持PDF、PNG、JPG格式");
    }
}
