import React, { Component, PropTypes } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';

function Welcome() {
  return (
    <div>
      <h1>Welcome</h1>
      <hr />
    </div>
  );
}

Welcome.propTypes = {
};

export default Welcome;
