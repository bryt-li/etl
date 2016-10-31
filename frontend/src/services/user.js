import request from '../utils/request';
import qs from 'qs';

import cfg from '../../app.config';


export async function login(params) {
	//return {response:..., err:...}
	return request(`${cfg.server}/login/`, {
		method: "POST",
		headers: {
			/*Must have this to make Nutz backend recognize.*/
			'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
		},
		body: qs.stringify(params)
	});
}

export async function logout() {
	//return {response:..., err:...}
	return request(`${cfg.server}/logout/`, {
		method: "POST"
	});
}
