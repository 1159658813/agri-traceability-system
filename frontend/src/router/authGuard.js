import { currentUser } from '../api/auth';

export async function checkManageAccess(to, from, next) {
  const adminRoute = to.path === '/manage' || to.path.startsWith('/manage/');
  const farmerRoute = to.path === '/farmer' || to.path.startsWith('/farmer/');
  if (!adminRoute && !farmerRoute) {
    next();
    return;
  }

  try {
    const account = await currentUser();
    if (adminRoute && account.roleCode === 'ADMIN') return next();
    if (farmerRoute && account.roleCode === 'FARMER') return next();
    next({ path: account.roleCode === 'ADMIN' ? '/manage' : '/farmer', replace: true });
  } catch (error) {
    next({ path: '/login', query: { redirect: to.fullPath }, replace: true });
  }
}
