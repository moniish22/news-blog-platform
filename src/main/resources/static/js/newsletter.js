// Quick client-side email check for newsletter forms (the server validates again).
document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('.newsletter-form').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      var input = form.querySelector('input[type="email"]');
      var ok = /^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(input.value.trim());
      input.classList.toggle('is-invalid', !ok);
      if (!ok) e.preventDefault();
    });
  });
});
