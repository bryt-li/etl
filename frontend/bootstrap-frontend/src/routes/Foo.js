import React, { Component, PropTypes } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';

function Foo() {
  return (
    <div className="row">
      <div className="col-md-6">.col-md-6</div>
      <div className="col-md-6"><button type="button" className="btn btn-primary">Primary</button>
</div>
    </div>
  );
}

Foo.propTypes = {
};

export default Foo;
