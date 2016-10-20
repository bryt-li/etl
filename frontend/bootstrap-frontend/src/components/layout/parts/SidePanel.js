import React, { Component, PropTypes } from 'react';
import { Link } from 'dva/router';
import { Menu, Icon } from 'antd'
const SubMenu = Menu.SubMenu

import styles from './SidePanel.less';

function SidePanel({menuItems}) {
  const menu = menuItems.map((item) => {
      return (
        <SubMenu title={<span><Icon type={item.icon} />{item.name}</span>}>
          {item.children.map((node) => {
            return (
              <Menu.Item>{node.name}</Menu.Item>
            )
          })}
        </SubMenu>
      )
    });

  return (
    <aside className={styles.panel}>
        <div className={styles.logo}><h1>AdminETL</h1></div>
        <Menu mode="inline" theme="dark">
          {menu}
        </Menu>
    </aside>
  );
}

SidePanel.propTypes = {
};

export default SidePanel;
