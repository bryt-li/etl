import React, { Component, PropTypes } from 'react';
import { Link } from 'dva/router';

import styles from './Footer.less';

function Footer() {
  return (
    <div className={styles.footer}>
      V创客 版权所有 © 2016 www.Mr-V.cn
    </div>
  );
}

Footer.propTypes = {
};

export default Footer;
