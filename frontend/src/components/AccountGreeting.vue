<template>
  <span class="account-greeting">Hi，{{ account ? (account.displayName || account.username) : '欢迎回来' }}</span>
</template>

<script>
import { currentUser } from '../api/auth';

export default {
  name: 'AccountGreeting',
  data() { return { account: null }; },
  async mounted() {
    try { this.account = await currentUser(); } catch (error) { this.account = null; }
  }
};
</script>

<style scoped>
.account-greeting { font-size: 14px; color: #315b45; font-weight: 600; }
</style>
