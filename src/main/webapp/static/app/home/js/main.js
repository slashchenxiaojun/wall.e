layui.use(['layer', 'form'], function() {
  var 
  $ = layui.jquery,
  form = layui.form(),
  layer = layui.layer;
  
  $('.create_project').on('click', function(){
    window.location.href = Global.base + '/create';
  });
  
  $('.work_project').on('click', function(){
    window.location.href = Global.base + '/project';
  });
});