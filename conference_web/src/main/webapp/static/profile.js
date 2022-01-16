(() => {
    const modal = document.querySelector('#profile-modal');
    const modalBg = document.querySelector('#profile-modal .modal--background');
    const viewBtns = document.querySelectorAll('.__view-btn');
    const paperUpload = document.querySelectorAll('input[name="paper-file"]');

    const hideModal = () => {
        modal.classList.remove('show');
    }

    const viewPaper = (index) => {
        modal.classList.add('show');
        fetch(`getPaper?paperId=${index}`)
            .then(response => response.json())
            .then(json => {
                json = JSON.parse(json);

                modal.querySelector('.modal--content__title').textContent = json['title'];
                modal.querySelector('.modal--content__presenter').querySelector('.__content').textContent = `${json['presenter']['lastName']} ${json['presenter']['firstName']}`;
                modal.querySelector('.modal--content__coauthors').querySelector('.__content').textContent =  json['coAuthors'] == null ? '' : json['coAuthors'].join(', ');

                let abstractHtml = "";
                json['abstr'].split('\n').filter(l => l !== '').forEach(l => abstractHtml += `<p>${l}</p>`);
                modal.querySelector('.modal--content__abstract').innerHTML = abstractHtml;
            })
            .catch(error => console.warn("Trying to access missing resource or error in parsing."));
    };

    if (modalBg) {
        modalBg.addEventListener('click', hideModal);
    }

    if (viewBtns) {
        viewBtns.forEach(f => f.addEventListener('click', () => {
            viewPaper(f.getAttribute("data-paper"));
        }));
    }

    const toggleUploadBtn = (elem) => {
        if (elem.value) {
            document.querySelector(`#paper-submit-${elem.getAttribute('id')}`).removeAttribute('disabled');
        } else {
            document.querySelector(`#paper-submit-${elem.getAttribute('id')}`).setAttribute('disabled', "true");
        }
    };

    if (paperUpload) {
        paperUpload.forEach(f => f.addEventListener('change', () => {
            toggleUploadBtn(f);
        }));
    }

    const statusInputs = document.querySelectorAll('select[name="verify-status"]');

    const updateStatus = (id, status) => {
        fetch(`verifyPaper?paperId=${id}&newStatus=${status}`)
            .then(resp => {
                if (resp.status === 200) {
                    window.alert('Dolgozat frissítése sikeres.')
                } else {
                    window.alert('Dolgozat frissítése sikertelen.')
                }
            })
            .catch(err => console.warn(err));
    };

    if (statusInputs) {
        statusInputs.forEach(statusInput => statusInput.addEventListener('change', () => {
            updateStatus(statusInput.parentNode.parentNode.querySelector('input[name="verify-id"]').value, statusInput.value);
        }));
    }
})();