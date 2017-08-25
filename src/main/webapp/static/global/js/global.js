// 全局参数与方法
Global = {};
Global.base = '${base}';

Global.getParamMapByUrl = function() {
  if ( window.location.search === "" ) return null;
  // 去除第一个`?`
  var value = window.location.search.substr(1);

  var paramArray = value.split('&');
  var result = new Map();
  for ( var i = 0; i < paramArray.length; i++ ) {
    result.set( paramArray[i].split("=")[0], paramArray[i].split("=")[1] );
  }
  return result;
}