import fetch from 'dva/fetch';

function checkStatus(response) {
  if (response.status >= 200 && response.status < 300) {
    return response;
  }

  const error = new Error(response.statusText);
  error.response = response;
  throw error;
}

function parseJSON(response) {
  return response.json();
}


/**
 * Requests a URL, returning a promise.
 *
 * @param  {string} url       The URL we want to request
 * @param  {object} [options] The options we want to pass to "fetch"
 * @return {object}           An object containing either "data" or "err"
 */
export default function request(url, options) {
  /*Must have credentials include to enable cookies*/
  return fetch(url, {...options, credentials: 'include'})
    .then(checkStatus)
    .then(parseJSON)
    .then((response) => ({ response }))
    .catch((err) => ({ err }));
}
