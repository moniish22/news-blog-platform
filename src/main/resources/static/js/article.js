// Article page: reading progress bar, read-time/bounce tracking and share tracking.
(function () {
  var meta = document.getElementById('article-meta');
  var csrfTag = document.querySelector('meta[name="_csrf"]');
  var csrf = csrfTag ? csrfTag.content : null;

  var bar = document.getElementById('read-progress');
  if (bar) {
    window.addEventListener('scroll', function () {
      var h = document.documentElement;
      var max = h.scrollHeight - h.clientHeight;
      bar.style.width = (max > 0 ? Math.min(100, (h.scrollTop / max) * 100) : 0) + '%';
    }, { passive: true });
  }

  var copy = document.querySelector('[data-copy-link]');
  if (copy && navigator.clipboard) {
    copy.addEventListener('click', function () {
      navigator.clipboard.writeText(window.location.href.split('#')[0]);
      copy.textContent = 'Link copied';
    });
  }

  if (!meta) return;            // not a published article: nothing to track
  var id = meta.dataset.articleId, slug = meta.dataset.slug, start = Date.now(), sent = false;

  function post(url, fields) {
    var fd = new FormData();
    Object.keys(fields).forEach(function (k) { fd.append(k, fields[k]); });
    if (csrf) fd.append('_csrf', csrf);
    navigator.sendBeacon(url, fd);
  }
  function sendTime() {
    if (sent) return;
    sent = true;
    post('/analytics/read-time', { articleId: id, seconds: Math.round((Date.now() - start) / 1000) });
  }
  document.addEventListener('visibilitychange', function () { if (document.visibilityState === 'hidden') sendTime(); });
  window.addEventListener('pagehide', sendTime);

  document.querySelectorAll('[data-share]').forEach(function (a) {
    a.addEventListener('click', function () { post('/article/' + encodeURIComponent(slug) + '/share', {}); });
  });
})();
