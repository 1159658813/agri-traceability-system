import { currentUser } from '../api/auth';

export async function checkManageAccess(to, from, next) {
  const isManage = to.path === '/manage' || to.path.startsWith('/manage/');
  if (!isManage) {
    next();
    return;
  }

  try {
    const account = await currentUser();
    if (account.roleCode === 'ADMIN') {
      next();
      return;
    }
    next({ path: '/', replace: true });
  } catch (error) {
    next({ path: '/login', query: { redirect: to.fullPath }, replace: true });
  }
}
