async function authRequest(path, options = {}) {
  const response = await fetch(`/api/auth/${path}`, {
    credentials: 'same-origin',
    cache: 'no-store',
    ...options
  });
  const result = await response.json();
  if (!response.ok || result.code !== 200) {
    throw new Error(result.message || '请求失败');
  }
  return result.data;
}

export function currentUser() {
  return authRequest('me');
}

export function logout(csrfToken) {
  return authRequest('logout', {
    method: 'POST',
    headers: { 'X-CSRF-Token': csrfToken }
  });
}
