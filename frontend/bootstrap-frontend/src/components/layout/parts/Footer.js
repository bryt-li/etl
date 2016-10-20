import React, { Component, PropTypes } from 'react';
import { Link } from 'dva/router';

import styles from './Footer.less';

var classNames = require('classnames');

function Footer() {
  return (
	<div className={styles.footer}>
		<div className={classNames(styles.container,"container")}>
			<div className="col-md-4">
				<h3>
					<span className="glyphicon glyphicon-user"></span>开发者信息
				</h3>
				<p>Lixin: 15388031573</p>
			</div>
			<div className="col-md-4">
				<h3>
					<span className="glyphicon glyphicon-info-sign"></span>帮助文档
				</h3>
				<p>任务查看和任务管理</p>
			</div>
			<div className="col-md-4">
				<h3>
					<span className="glyphicon glyphicon-send"></span>联系方式
				</h3>
				<p>电子邮箱: lixin@ngblab.org</p>
			</div>
		</div>
	</div>
  );
}

Footer.propTypes = {
};

export default Footer;
