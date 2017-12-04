form = null;
layui.use(['layer', 'form'], function() {
  var
  $ = layui.jquery,
  tree = layui.tree,
  layer = layui.layer;

  // 使用 form 作为全局对象，ztree代码需要使用
  form = layui.form();
  layer.msg('Welcome use wall.e');
  $('.walle').on('click', function() {
    window.location.href = Global.base + '/dbmodel' + window.location.search;
  });

  // ---------init---------
  // 获取project的pattern
  var pattern = $('select[name="project"] option:selected').attr('pattern');
  if(pattern == 'normal') {
    $('select[name="type"]').find('option').eq(1).attr('selected', true);
  }else if(pattern == 'simulation'){
    $('select[name="type"]').find('option').eq(2).attr('selected', true);
  }
  // init project select
  var projectId = $('body').attr('project-id');
  $('select[name="project"]').find('option[value=' + projectId + ']').attr('selected', true);
  form.render();
  // ---------init---------

  // 重新初始化ZTree,select[name="type"]
  form.on('select(project)', function(data){
    var url = window.location.href.substr(0, window.location.href.indexOf('?')) + '?projectId=' + data.value;
    window.location.href = url;
  });

  form.on('select(type)', function(data){

  });

  $('.create-folder').on('click', function(){
    layer.prompt({
      title: '请输入Collections名称',
      area: ['400px', '50px'] //自定义文本域宽高
    }, function(value, index, elem){
      $.when($.getJSON(Global.base + '/project/createFolder', {
        'project.id': $('body').attr('project-id'),
        'folder.name': value
      })).done(function(r) {
        if(r.code == 0) {
          window.location.reload();
        } else {
          layer.msg('保存失败');
        }
      });
    });
  });

}); // layui-fun

$(function() {
  var zTreeObj;
  // 通过url判断
  // 返回projectId=x
  var project_id = window.location.search.substr(1).split('&').find( function(e) { e = e.split('=')[0]; return e === 'projectId'} );
  project_id = project_id.split('=')[1];
  if (project_id == undefined) project_id = 1;
  $('body').attr('project-id', project_id);

  $.when($.getJSON(Global.base + '/project/getTreeFolder', {'project.id': project_id}))
  .done(function(r) {
    if(r.code == 0) {
      // zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
      var setting = {
        callback: {
          onClick: zTreeOnClick,
          onRightClick: zTreeOnRightClick
        }
      };
      var zNodes = r.data;
      $.fn.zTree.init($("#interface_tree"), setting, zNodes);
    }
  });

  // -----setting listen
  $(document).on('click', '.test', function(e) {
    var interfaceId = $(this).parent().parent().attr('data-id');
    var icon = $(this);
    var tr = $(this).parent().parent();
    // 执行接口时改变颜色 #f8f8f8 ---> #F7B824
    tr.attr('style', 'background-color: #F7B824');
    icon.html('&#xe63d;');
    // 添加动画属性
    icon.attr('style', 'display: inline-block;');
    $.when($.getJSON(Global.base + '/project/invoke', {'interface.id': interfaceId}))
    .done(function(r) {
      if(r.code == 0) {
        // 显示list绿色
        tr.attr('style', 'background-color: #5FB878');
        icon.html('&#xe623;');
        icon.attr('style', null);
      } else {
        // 显示list红色
        tr.attr('style', 'background-color: #FF5722');
        icon.html('&#xe623;');
        icon.attr('style', null);
      }
    }).fail(function() {
      // 显示list红色
      // 系统错误
      tr.attr('style', 'background-color: #FF5722');
      icon.html('&#xe623;');
      icon.attr('style', null);
    });
  });

  $(document).on('click', '.setting', function(e){
    var interfaceId = $(this).parent().parent().attr('data-id');
    var settingIndex = layer.open({
      type: 2,
      area: ['1024px', '650px'],
      btn: ['保存', '关闭'],
      fixed: false, //不固定
      maxmin: true,
      content: Global.base + '/project/interfaceForm?projectId=' + $('body').attr('project-id') + '&interfaceId=' + interfaceId,
      yes: function(index, dlayer) {
        // 获取表单信息提交
        var doc = dlayer.find('iframe')[0].contentWindow.document;
        // 过滤input[type=checkbox].value
        var data = $(doc).find('form').serialize();
        var url = Global.base + '/project/saveInterface';
        var deferred = $.getJSON(url, data);
        $.when(deferred).done(function(result) {
          if(result.code == 0) {
            layer.close(settingIndex);
            layer.msg('OK');
          } else {
            layer.msg(result.msg);
          }
        }).fail(function() {
          layer.msg('Oop! 系统错误');
        });
      }
    });
//    layer.open({
//      type: 2,
//      area: ['700px', '530px'],
//      fixed: false, //不固定
//      maxmin: true,
//      content: Global.base + '/project/interfaceForm?projectId=' + $('body').attr('project-id') + '&interfaceId=' + interfaceId
//    });
  });

  $(document).on('click', '.copy', function(e){
    var interfaceId = $(this).parent().parent().attr('data-id');
    layer.confirm('确认要复制接口吗?', {icon: 3, title:'提示'}, function(index) {
      $.when($.getJSON(Global.base + '/project/duplicateInterface', {
        'interface.id': interfaceId,
        'project.id': $('body').attr('project-id'),
        'folder.id': $('#interface_tree').attr('folder-id')
      })).done(function(r) {
        if(r.code == 0) {
          // 刷新table
          renderInterfaceTable(r.data);
        } else {
          layer.msg('复制接口失败(' + r.msg + ')');
        }
      });
      layer.close(index);
    });
  });

  $(document).on('click', '.simulation', function(e){
    var interfaceId = $(this).parent().parent().attr('data-id');
    layer.open({
      type: 2,
      area: ['700px', '530px'],
      fixed: false, //不固定
      maxmin: true,
      content: Global.base + '/project/interfaceSimulationData?projectId=' + $('body').attr('project-id') + '&interfaceId=' + interfaceId
    });
  });

  // -----
  var zTreeOnRightClick = function(event, treeId, treeNode) {
    if(treeNode.interface_id != null) {
      $('#interface_tree').attr('folder-id', null);
      $('#interface_tree').attr('interface-id', treeNode.interface_id);
      $('#interface_tree').attr('interface-name', treeNode.name);
    } else {
      $('#interface_tree').attr('folder-id', treeNode.folder_id);
      $('#interface_tree').attr('interface-id', null);
      $('#interface_tree').attr('interface-name', null);
    }
    collectionEvent();
    interfaceEvent();
  };

  var zTreeOnClick = function(event, treeId, treeNode) {
    if(treeNode.interface_id != null) { // get one interface
      $('#interface_tree').attr('folder-id', null);
      $('#interface_tree').attr('interface-id', treeNode.interface_id);
      var intefaceId = $('#interface_tree').attr('interface-id');
      $.when($.getJSON(Global.base + '/project/getInterface', {
        'interface.id': intefaceId
      })).done(function(r) {
        if(r.code == 0) {
          // 刷新table
          renderInterfaceTable(r.data);
        } else {
          layer.msg('获取接口失败(' + r.msg + ')');
        }
      });
    } else { // get all interface
      $('#interface_tree').attr('folder-id', treeNode.folder_id);
      $('#interface_tree').attr('interface-id', null);
      var folderId = $('#interface_tree').attr('folder-id');
      $.when($.getJSON(Global.base + '/project/getInterfaceByFolder', {
        'project.id': $('body').attr('project-id'),
        'folder.id': folderId,
      })).done(function(r) {
        if(r.code == 0) {
          // 刷新table
          renderInterfaceTable(r.data);
        } else {
          layer.msg('获取接口列表失败(' + r.msg + ')');
        }
      });
    }
  }

  var renderInterfaceTable = function(data) {
    $('.interfaceTable').html(null);
    if (Object.prototype.toString.call(data) == '[object Array]') {
      for (v in data) {
        var interfaceNameAndCode = data[v].name + (data[v].code == null ? '' : ' (' + data[v].code + ')');
        // 排序号
//         + " ----- " + ( data[v].seq == null ? '0' : data[v].seq );
        var tr = '<tr class="interface" data-id="' + data[v].id + '"><td><input type="checkbox"></td>'
        + '<td>' + interfaceNameAndCode + '</td>'
        + '<td class="operation">'
        + '<i class="layui-icon layui-anim layui-anim-rotate layui-anim-loop test">&#xe623;</i>' //
        + '<i class="layui-icon setting">&#xe620;</i>'
        + '<i class="layui-icon copy">&#xe630;</i>'
//        + '<i class="layui-icon simulation">&#xe60b;</i>'
        + '</td></tr>';
        $('.interfaceTable').append(tr);
      }
    } else {
      var interfaceNameAndCode = data.name + (data.code == null ? '' : ' (' + data.code + ')');
      var tr = '<tr class="interface" data-id="' + data.id + '"><td><input type="checkbox"></td>'
      + '<td>' + interfaceNameAndCode + '</td>'
      + '<td class="operation">'
      + '<i class="layui-icon layui-anim layui-anim-rotate layui-anim-loop test">&#xe623;</i>' //
      + '<i class="layui-icon setting">&#xe620;</i>'
      // + '<i class="layui-icon copy">&#xe630;</i>'
//      + '<i class="layui-icon simulation">&#xe60b;</i>'
      + '</td></tr>';
      $('.interfaceTable').append(tr);
    }
  }

  // use contextjs
  context.init({preventDoubleContext: false});

  /* 右键列表适配，只能支持2次菜单 */
  // Collection的右键列表
  var collectionEvent = function() {
    context.attach('#interface_tree li a.level0', [
      {
        header: '集合管理'
      },
      {
        text: '重命名集合',
        target:'_blank',
        action: function(e) {
          e.preventDefault();
          layer.prompt({
            title: '请输入集合名称',
            area: ['400px', '50px'] //自定义文本域宽高
          }, function(value, index, elem){
            var folderId = $('#interface_tree').attr('folder-id');
            $.when($.getJSON(Global.base + '/project/renameFolder', {
              'project.id': $('body').attr('project-id'),
              'folder.id': folderId,
              'folder.name': value
            })).done(function(r) {
              if(r.code == 0) {
                window.location.reload();
              } else {
                layer.msg('保存失败(' + r.msg + ')');
              }
            });
          });
        }
      },
      {
        text: '删除集合',
        target:'_blank',
        action: function(e) {
          e.preventDefault();
          layer.confirm('确定删除该Collections吗?', {
            icon: 3, title:'删除'
          }, function(value, index, elem){
            var folder_id = $('#interface_tree').attr('folder-id');
            if(folder_id == null || folder_id == '') {
              layer.msg('请选择folder');
              return;
            }
            $.when($.getJSON(Global.base + '/project/deteleFolder', {
              'folder.id': folder_id,
              'folder.name': value
            })).done(function(r) {
              if(r.code == 0) {
                window.location.reload();
              } else {
                layer.msg('删除失败');
              }
            });
          });
        }
      },
      {
        header: '接口管理'
      },
      {
        text: '新增接口',
        target:'_blank',
        action: function(e) {
          e.preventDefault();
          layer.prompt({
            title: '请输入接口名称',
            area: ['400px', '50px'] //自定义文本域宽高
          }, function(value, index, elem){
            var folderId = $('#interface_tree').attr('folder-id');
            $.when($.getJSON(Global.base + '/project/createInterfaceQuick', {
              'project.id': $('body').attr('project-id'),
              'folder.id': folderId,
              'interface.name': value
            })).done(function(r) {
              if(r.code == 0) {
                window.location.reload();
              } else {
                layer.msg('保存失败(' + r.msg + ')');
              }
            });
          });
        }
      }
    ]);
  }
  // 接口的右键列表
  var interfaceEvent = 
    context.attach('#interface_tree li a.level1', [
      {
        header: '接口管理'
      },
      {
        text: '重命名接口',
        target:'_blank',
        action: function(e) {
          e.preventDefault();
          layer.prompt({
            value: $('#interface_tree').attr('interface-name'),
            title: '请输入接口名称',
            area: ['400px', '50px'] //自定义文本域宽高
          }, function(value, index, elem){
            var interfaceId = $('#interface_tree').attr('interface-id');
            $.when($.getJSON(Global.base + '/project/renameInterface', {
              'project.id': $('body').attr('project-id'),
              'interface.id': interfaceId,
              'interface.name': value
            })).done(function(r) {
              if(r.code == 0) {
                window.location.reload();
              } else {
                layer.msg('保存失败(' + r.msg + ')');
              }
            });
          });
        }
      },
      {
        text: '删除接口',
        target:'_blank',
          action: function(e) {
            e.preventDefault();
            layer.confirm('确定删除该接口吗?', {
              icon: 3, title:'删除'
            }, function(value, index, elem){
              var interface_id = $('#interface_tree').attr('interface-id');
              if(interface_id == null || interface_id == '') {
                layer.msg('请选择接口');
                return;
              }
              $.when($.getJSON(Global.base + '/project/deteleInterface', {
                'interface.id': interface_id
              })).done(function(r) {
                if(r.code == 0) {
                  window.location.reload();
                } else {
                  layer.msg('删除失败');
                }
              });
            });
          }
      }
    ]);
  
  // ----
});