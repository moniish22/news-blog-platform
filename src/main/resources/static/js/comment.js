// Shows/hides the inline reply form under a comment.
document.addEventListener('click', function (e) {
  var btn = e.target.closest('[data-reply-toggle]');
  if (!btn) return;
  var form = document.getElementById(btn.dataset.replyToggle);
  if (form) {
    form.classList.toggle('d-none');
    if (!form.classList.contains('d-none')) form.querySelector('textarea').focus();
  }
});
