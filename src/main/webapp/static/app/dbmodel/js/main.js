layui.use(['layer', 'form', 'laypage'], function() {
  var 
  $ = layui.jquery,
  tree = layui.tree,
  form = layui.form,
  laypage = layui.laypage,
  layer = layui.layer;

  laypage({
	  cont: 'paginate',
	  pages: $('#paginate').attr('page'),
	  curr: $('#paginate').attr('curr'),
	  skip: true,
	  jump: function(obj, first){
		  if(first) return;
		  // 得到了当前页，用于向服务端请求对应数据
		  var curr = obj.curr;
		  var url = window.location.href;
		  if (url.indexOf('pageNumber') == -1 && curr != 1) {
			  url += '&pageNumber=' + curr;
			  window.location.replace(url);
		  } else {
			  url = url.substr(0, url.indexOf('pageNumber') + 'pageNumber='.length) + curr;
			  window.location.replace(url);
		  }
	  }
  });
  
  // create model
  $('.create-model').on('click', function(){
    layer.open({
      type: 2,
      area: ['1024px', '530px'],
      btn: ['保存', '关闭'],
      fixed: false, //不固定
      maxmin: true,
      content: 'dbmodel/create_model' + window.location.search,
      yes: function(index, dlayer) {
        // 获取表单信息提交
        var doc = dlayer.find('iframe')[0].contentWindow.document;
        // 过滤input[type=checkbox].value
        var model_data = $(doc).find('.dbmodel-form').serialize();
        var model_items_data = $(doc).find('.dbmodel-item-form').serialize();
        var url = Global.base + '/dbmodel/save';
        var deferred = $.getJSON(url, model_data + '&' + model_items_data);
        $.when(deferred).done(function(result) {
          if(result.code == 0) {
            window.location.reload();
          } else {
            layer.msg('Oop! 保存失败');
          }
        }).fail(function() {
          layer.msg('Oop! 系统错误');
        });
      }
    });
    return false;
  });

  // 映射model
  $('.mapping-model').on('click', function(){
    layer.open({
      type: 2,
      area: ['1024px', '530px'],
      btn: ['保存', '关闭'],
      fixed: false, //不固定
      maxmin: true,
      content: 'dbmodel/mapping_model?projectId=' + Global.projectId,
      yes: function(index, dlayer) {
        // 获取表单信息提交
        var doc = dlayer.find('iframe')[0].contentWindow.document;
        // 过滤input[type=checkbox].value
        var data = $(doc).find('.mapping-model-form').serialize();
        var url = Global.base + '/dbmodel/mapping';
        var deferred = $.getJSON(url, data);
        $.when(deferred).done(function(result) {
          if(result.code == 0) {
            window.location.reload();
          } else {
            layer.msg('Oop! 保存失败');
          }
        }).fail(function() {
          layer.msg('Oop! 系统错误');
        });
      }
    });
    return false;
  });

  // delete model
  $('.delete-form').on('click', function(){
    var id = $(this).attr('data-id');
    layer.confirm('确定删除该数据表吗?', {
      icon: 3, title:'删除'
    }, function(value, index){
      var deferred = $.getJSON('/dbmodel/delete/' + id);
      $.when(deferred).done(function(r) {
        if(r.code == 0) {
          window.location.reload();
        } else {
          layer.msg('删除失败');
        }
      });
    });
    return false;
  });

  // generate DBSql and syn to db
  $('.syn-db').on('click', function(){
    var id = $(this).attr('data-id');
    var dbName = $('input[name="dbName"]').val();
    if(dbName == null || dbName == '') {
      layer.msg('必须先设置生成DB的数据库名称'); return;
    }
    layer.confirm('确定同步数据表吗，这会删除原有表所有的数据?', {
      icon: 3, title:'同步数据表'
    }, function(value, index){
      var deferred = $.getJSON('/dbmodel/syndb', {
        modelId: id,
        dbName: dbName
      });
      $.when(deferred).done(function(r) {
        if(r.code == 0) {
          layer.msg('同步成功');
          setTimeout(function(){
            window.location.reload()
          }, 1000);
        } else {
          layer.msg('同步数据表失败');
        }
      });
    });
    return false;
  });

  // generate code
  $('.gen-code').on('click', function(){
    var id = $(this).attr('data-id');
    layer.confirm('确定要生成代码吗?', {
      icon: 3, title:'生成代码'
    }, function(value, index){
      var deferred = $.getJSON('/dbmodel/generate/' + id);
      $.when(deferred).done(function(r) {
        if(r.code == 0) {
          layer.msg('生成代码完成');
          setTimeout(function(){
            window.location.reload()
          }, 1000);
        } else {
          layer.msg('生成代码失败');
        }
      });
    });
    return false;
  });

  // quick generate
  $('.quickGenerate').on('click', function(){
    var dbName = $('input[name="dbName"]').val();
    if(dbName == null || dbName == '') {
      layer.msg('必须先设置生成DB的数据库名称'); return;
    }
    layer.confirm('确定要使用快速生成吗?', {
      icon: 3, title:'快速生成'
    }, function(value, index){
      var deferred = $.getJSON('/dbmodel/quickGenerate/' + Global.projectId, {
        dbName: dbName
      });
      $.when(deferred).done(function(r) {
        if(r.code == 0) {
          layer.msg('生成代码完成');
          setTimeout(function(){
            window.location.reload()
          }, 1000);
        } else {
          layer.msg('生成代码失败');
        }
      });
    });
    return false;
  });

});