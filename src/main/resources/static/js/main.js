// Global behaviour: auto-dismiss flash messages and confirm destructive actions.
document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('.alert-dismissible').forEach(function (el) {
    setTimeout(function () { if (window.bootstrap) bootstrap.Alert.getOrCreateInstance(el).close(); }, 7000);
  });
  document.querySelectorAll('form[data-confirm]').forEach(function (form) {
    form.addEventListener('submit', function (e) { if (!confirm(form.dataset.confirm)) e.preventDefault(); });
  });
});
