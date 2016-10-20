import React, { Component, PropTypes } from 'react';
import { Link } from 'dva/router';

import styles from './Header.less';

var classNames = require('classnames');

function Header({user, allowRegister}) {

  return (
  	<header className={styles.header} role="header">
        <div className={classNames(styles.container,'container')}>
            <a href="#" className={classNames(styles.navbarBrand,'navbar-brand', 'pull-left')}>ETL Admin</a>
            <button className="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span className="glyphicon glyphicon-align-justify"></span>
            </button>
            <nav className="navbar-collapse collapse" role="navigation">
                <ul className={classNames(styles.navbarNav,"navbar-nav", "nav")}>
                    <li><a href="">首页</a></li>
                    <li><a href="">登录</a></li>
                    <li><a href="">注销</a></li>                    
                    <li><a href="">控制面板</a></li>
                </ul>
            </nav>
        </div>
    </header>
  );
}

Header.propTypes = {
};

export default Header;
