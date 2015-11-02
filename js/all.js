function goToProse(repo, page) {
    window.location = repo.replace(/^https?:\/\/[^\/]*\//i,'http://prose.io/#') + '/edit/gh-pages/' + page;
}

function goToGitHub(repo, page) {
    window.location = repo + '/edit/gh-pages/' + page;
}
