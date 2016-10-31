'use strict';

var qs = require('qs');

//var cfg = require('../app.config.js');
//console.log(`Mocking HTTP service on port 8989 with base dir: ${cfg.server}`);

module.exports = {

	'POST /login': function (req, res) {
		setTimeout(function () {
			var request = qs.parse(req.body);

			console.log(`login: account=${request.account} password=${request.password}`);

			if(request.account=='ghost'){
				res.json({
					ok: false,
					err: '服务器错误',
					data: null
				});
			}else{
				res.json({
					ok: true,
					err: null,
					data: {id:1, name:'foo',role:'管理员'}
				});
			}
		}, 300);
	},

	'POST /logout': function (req, res) {
		res.json({
			ok: true,
			err: null,
			data: null
		});
	},

	'GET /foo': function (req, res) {
		res.json({foo:'bar'});
	},

};
