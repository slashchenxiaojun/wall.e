layui.use(['layer', 'form'], function() {
  var 
  $ = layui.jquery,
  tree = layui.tree,
  form = layui.form(),
  layer = layui.layer;

  layer.msg('Welcome use wall.e');

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

  $('.detele-folder').on('click', function(){
    
  });

}); // layui-fun

$(function() {
  var zTreeObj;
  // 通过url判断
  var project_id = window.location.search.substr('?projectId='.length);
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
      console.log(r.data)
      $.fn.zTree.init($("#interface_tree"), setting, zNodes);
    }
  });

  var zTreeOnRightClick = function(event, treeId, treeNode) {
    if(treeNode.interface_id != null) {
      $('#interface_tree').attr('folder-id', null);
      $('#interface_tree').attr('interface-id', treeNode.interface_id);
    } else {
      $('#interface_tree').attr('folder-id', treeNode.folder_id);
      $('#interface_tree').attr('interface-id', null);
    }
    collectionEvent();
    interfaceEvent();
  };

  var zTreeOnClick = function(event, treeId, treeNode) {
    if(treeNode.interface_id != null) {
      $('#interface_tree').attr('folder-id', null);
      $('#interface_tree').attr('interface-id', treeNode.interface_id);
    } else {
      $('#interface_tree').attr('folder-id', treeNode.folder_id);
      $('#interface_tree').attr('interface-id', null);
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
  var interfaceEvent = context.attach('#interface_tree li a.level1', [
    {
      header: '接口管理'
    },
    {
      text: '重命名接口',
      target:'_blank',
      action: function(e) {
        e.preventDefault();
        layer.prompt({
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