'use strict';

module.exports = {

	'GET /api/example': function (req, res) {
		setTimeout(function () {
			res.json({
				success: true,
				data: ['foo', 'bar', 'foo'],
			});
		}, 500);
	},

	'GET /api/foo': function (req, res) {
		res.json({
			success: true,
			data: ['foo', 'bar'],
		});
	},

};
