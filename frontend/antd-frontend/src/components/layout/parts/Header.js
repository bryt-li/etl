import React, { Component, PropTypes } from 'react';
import { Link } from 'dva/router';

import { Row, Col, Icon, Menu, Dropdown } from 'antd'

import styles from './Header.less';

const SubMenu = Menu.SubMenu;
const MenuItemGroup = Menu.ItemGroup;

function Header({user, allowRegister}) {

  return (
  	<div className={styles.header}>
        <Menu className={styles.menu}
        mode="horizontal">

        {
          user ?
          (
            <SubMenu title={<span><Icon type="user" />{user.name}</span>}>
              <Menu.Item key="setting:1">个人信息</Menu.Item>
              <Menu.Item key="setting:2">帐户安全</Menu.Item>
              <Menu.Divider />
              <Menu.Item key="setting:3">注销</Menu.Item>
            </SubMenu>
          ) 
          : null
        }
          
        {
          user==null?
          (
            <Menu.Item key="login">
              <Icon type="windows" />登录
            </Menu.Item>
          )
          : null
        }

        { 
          user==null && allowRegister ?
          (
            <Menu.Item key="register">
              <Icon type="team" />注册
            </Menu.Item>          )
          : null
        }

          <Menu.Item key="help">
            <Icon type="question" />帮助
          </Menu.Item>
        </Menu>
    </div>
  );
}

Header.propTypes = {
};

export default Header;
