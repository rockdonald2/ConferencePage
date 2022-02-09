(() => {
    // header logic
    const header = document.querySelector('.header');
    const handleScroll = () => {
        const offset = window.scrollY;
        const PAGE_OFFSET = 75;

        offset > PAGE_OFFSET ? header.classList.add('fixed') : header.classList.remove('fixed');
    }
    window.addEventListener('scroll', handleScroll);

    // scrollback
    const scrollback = document.querySelector('.scrollback');
    const showScrollBack = () => {
        const offset = window.scrollY;
        const PAGE_OFFSET = 250;

        offset > PAGE_OFFSET ?
            scrollback.classList.add('show') :
            scrollback.classList.remove('show');
    }
    window.addEventListener('scroll', showScrollBack);

    // mobile menu logic
    const hamburger = document.querySelector('.header--hamburger');
    const mobileMenu = document.querySelector('.header--menu');
    const toggleMobileMenu = () => {
        mobileMenu.classList.toggle('active');
        hamburger.classList.toggle('active');
    };
    if (hamburger && mobileMenu) {
        hamburger.addEventListener('click', toggleMobileMenu);
    }
    const submenuBtn = document.querySelector('#submenuBtn');
    const submenu = document.querySelector('.header--menu--dropdown');
    const toggleSubMenu = () => {
        submenu.classList.toggle('active');
    }
    if (submenu && submenuBtn) {
        submenuBtn.addEventListener('click', toggleSubMenu);
    }

    // accordion logic
    const accordions = document.querySelectorAll('.accordion');

    const toggleAnswer = (e) => {
        if (!e.target.classList.contains('active')) {
            e.target.classList.add('active');
        } else {
            e.target.classList.remove('active');
        }
    }

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

    // file input logic
    const fileInput = document.querySelectorAll('input[type="file"]');

    const handleFileInput = (elem) => {
        if (elem.value) {
            const splitValue = elem.value.split('\\');
            document.querySelector(`label[for="${elem.getAttribute("id")}"]`).querySelector('.__input-text').textContent = splitValue[splitValue.length - 1];
        }
    }

    if (fileInput) {
        fileInput.forEach(f => f.addEventListener('change', () => handleFileInput(f)));
    }
})();