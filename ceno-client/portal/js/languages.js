let languages = (function () {

// A global variable containing the current locale. Only written to within this module.
let globalLocale = 'en';

/**
 * Set the text content of a given element to some translated string.
 * @param {string} elemId - The id attribute of the element to access
 * @param {string} property - The name of the property of the element to set, e.g. textContent or innerHTML
 * @param {string} locale - The locale of the language to use, e.g. en or fr
 * @param {string} stringId - The key in locale/all.json for the string to use
 * @return {boolean} true if the attribute could be set or defaulted to English, else false
 */
function setText(elemId, property, locale, stringId) {
  let elem = document.getElementById(elemId);
  if (elem && LANGUAGES.hasOwnProperty(locale) && LANGUAGES[locale].hasOwnProperty(stringId)) {
    elem[property] = LANGUAGES[locale][stringId];
  } else if (elem && LANGUAGES.en.hasOwnProperty(stringId)) {
    elem[property] = LANGUAGES.en[stringId];
  } else if (elem) {
    // For debugging purposes only
    elem[property] = 'INVALID STRING ID ' + stringId;
  } else {
    return false;
  }
  return true;
}

function setLocale(locale) {
  globalLocale = locale;
  document.getElementById('currentLocale').textContent = locale;
  let currentStatus = navigation.getPortalStatus();
  navigation.setConnectivityStatus(currentStatus);
}

/**
 * Set all the visible text content on the channel selection page.
 * @param {string} locale - The locale of the language to use, e.g. en or fr
 */
function setChannelText(locale) {
  setLocale(locale);
  if (!setText('chooseChannelHeader', 'textContent', locale, 'chooseChannel')) {
    console.log('Could not set "Choose Channel" header text.');
  }
}

/**
 * The articles page doesn't have any preset content, so we just set the global locale when
 * a new language is selected.
 * @param {string} locale - The locale of the language to use, e.g. en or fr
 */
function setArticleText(locale) {
  setLocale(locale);
}

/**
 * Set all the visible text content on the main/index/home page of the portal.
 * @param {string} locale - The locale of the language to use, e.g. en or fr
 */
function setIndexText(locale) {
  setLocale(locale);
  if (!setText('whatNextHeader', 'textContent', locale, 'whatNext')) {
    console.log('Could not set "What Next" header text.');
  }
  if (!setText('browseChannelsHeader', 'textContent', locale, 'browseChannels')) {
    console.log('Could not set "Browse Channels" header text.');
  }
  if (!setText('browseChannelsParagraph', 'textContent', locale, 'browseChannelsDescription')) {
    console.log('Could not set "Browse Channels" paragraph text.');
  }
  if (!setText('requestSiteHeader', 'textContent', locale, 'requestSite')) {
    console.log('Could not set "Request a Site" header text.');
  }
  if (!setText('learnCENOHeader', 'textContent', locale, 'learnMore')) {
    console.log('Could not set "Learn More" Header text.');
  }
  if (!setText('learnCENOParagraph', 'textContent', locale, 'learnMoreDescription')) {
    console.log('Could not set "Learn More" paragraph text.');
  }
  if (!setText('tellMeMore', 'textContent', locale, 'learnMoreButton')) {
    console.log('Could not set "Learn More" button text.');
  }
}

/**
 * A helper function to set the text on whatever page we're on.
 * @param {string} locale - The locale of the language to use, e.g. en or fr
 */
function setPortalText(locale) {
  setIndexText(locale);
  setChannelText(locale);
  setArticleText(locale);
}

// When a language is selected, get its locale and reset the text on the page.
// TODO - Expand this to work on all pages.
let languageOptions = document.getElementsByClassName('languageSelect');
for (let i = 0, len = languageOptions.length; i < len; i++) {
  let option = languageOptions[i];
  option.addEventListener('click', () => {
    globalLocale = option.getAttribute('data-language');
    setPortalText(globalLocale);
  });
}

/**
 * For use by consumers of this module, get the currently selected locale.
 * @return {string} the current locale, like 'en' or 'fr'.
 */
function getLocale() {
  return globalLocale;
}

return {
  setText,
  setChannelText,
  setArticleText,
  setIndexText,
  getLocale
};

})();