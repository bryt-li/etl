import React, { Component, PropTypes } from 'react';
import { Link } from 'dva/router';
import { connect } from 'dva';

import styles from './Header.less';

var classNames = require('classnames');

function Header(props) {

    function handleLogout(e) {
        e.preventDefault();
        props.dispatch({ type: 'app/logout' });
    }

    return (
    	<header className={styles.header} role="header">
        <div className={classNames(styles.container,'container')}>
            <a href="#" className={classNames(styles.navbarBrand,'navbar-brand', 'pull-left')}>ETL Admin</a>
            <button className="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span className="glyphicon glyphicon-align-justify"></span>
            </button>
            <nav className="navbar-collapse collapse" role="navigation">
            {
                props.name?
                (
                <ul className={classNames(styles.navbarNav,"navbar-nav", "nav")}>
                    <li><Link to="/">首页</Link></li>
                    <li><a href="#" onClick={handleLogout}>{'注销 '+props.name}</a></li>
                    <li><Link to="/etl">控制面板</Link></li>
                </ul>
                )
                :
                (
                <ul className={classNames(styles.navbarNav,"navbar-nav", "nav")}>
                    <li>
                    <Link to="/">首页</Link></li>
                    <li><Link to="/login">登录</Link></li>
                    <li><Link to="/etl">控制面板</Link></li>
                </ul>
                )
            }                
            </nav>
        </div>
    </header>
    );
}

Header.propTypes = {
};

const mapStateToProps = (state) => ({
    name: state.app.name,
    role: state.app.role
});


export default connect(mapStateToProps)(Header);
