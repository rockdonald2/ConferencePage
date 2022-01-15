(function () {
    // header logic
    window.addEventListener('scroll', handleScroll);
    const header = document.querySelector('.header');

    function handleScroll() {
        const offset = window.scrollY;
        const PAGE_OFFSET = 75;

        offset > PAGE_OFFSET ? header.classList.add('fixed') : header.classList.remove('fixed');
    }

    // scrollback
    window.addEventListener('scroll', showScrollBack);
    const scrollback = document.querySelector('.scrollback');

    function showScrollBack() {
        const offset = window.scrollY;
        const PAGE_OFFSET = 250;

        offset > PAGE_OFFSET ?
            scrollback.classList.add('show') :
            scrollback.classList.remove('show');
    }

    // mobile menu logic
    const hamburger = document.querySelector('.header--hamburger');
    const mobileMenu = document.querySelector('.header--menu');

    if (hamburger && mobileMenu) {
        hamburger.addEventListener('click', toggleMobileMenu);
    }

    function toggleMobileMenu() {
        mobileMenu.classList.toggle('active');
        hamburger.classList.toggle('active');
    }

    const submenuBtn = document.querySelector('#submenuBtn');
    const submenu = document.querySelector('.header--menu--dropdown');

    if (submenu && submenuBtn) {
        submenuBtn.addEventListener('click', toggleSubmenu);
    }

    function toggleSubmenu() {
        submenu.classList.toggle('active');
    }

    // accordion logic
    const accordions = document.querySelectorAll('.accordion');

    if (accordions) {
        accordions.forEach((acc) => {
            acc.addEventListener('click', toggleAnswer);
        });
    }

    const conditionsAcc = document.querySelectorAll('.conditions-page--toggle');

    if (conditionsAcc) {
        conditionsAcc.forEach((acc) => {
            acc.addEventListener('click', toggleAnswer);
        });
    }

    function toggleAnswer(e) {
        if (!e.target.classList.contains('active')) {
            e.target.classList.add('active');
        } else {
            e.target.classList.remove('active');
        }
    }

    // file input logic
    const fileInput = document.querySelectorAll('input[type="file"]');

    if (fileInput) {
        fileInput.forEach(f => f.addEventListener('change', handleFileInput));
    }

    function handleFileInput(e) {
        if (this.value) {
            const splitValue = this.value.split('\\');
            document.querySelector(`label[for="${this.getAttribute("id")}"]`).querySelector('.__input-text').textContent = splitValue[splitValue.length - 1];
        }
    }
})();