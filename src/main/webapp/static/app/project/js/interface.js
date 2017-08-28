layui.use(['layer', 'form', 'element'], function() {
  var
  $       = layui.jquery,
  form    = layui.form(),
  element = layui.element(),
  layer   = layui.layer;

  var projectId = Global.getParamMapByUrl().get("projectId");

  var disabled = function(doms) {
    $.each(doms, function(k, v) {
      $(v).attr('disabled', 'disabled');
      $(v).val(null);
    });
  }

  var enabled = function(doms) {
    $.each(doms, function(k, v) {
      $(v).removeAttr('disabled');
    });
  }

  $(document).on('focus', 'input[name="extend"]', function() {
  });
  $(document).on('blur', 'input[name="extend"]', function() {
    var type = $(this).parent().prev().children().val();
    if ( type === 'Enum' ) {
      $(this).next().next().val($(this).val());
    } else if ( type === 'Object' || type === 'Array' ) {
      $(this).next().val($(this).val());
    }
  });

  form.on('select(param_type)', function(data){
    var td = $(data.elem).parent();
    // 补充 input
    var extend = td.next().children();
    // 大小 input
    var max = td.next().next().children();
    // 特殊格式
    var format = td.next().next().next().children();
    if(data.value == 'String') {
      // enabled([max, format]);
      // disabled(extend);
      max.val(64);
    } else if(data.value == 'Enum') {
      // enabled(extend);
      // disabled([max, format]);
    } else if(data.value == 'Number') {
      // enabled([max, format]);
      // disabled(extend);
      // 32位整理最大值
      max.val(2147483647);
    } else if(data.value == 'Boolean') {
      // disabled([max, format, extend]);
    } else if(data.value == 'Date') {
      // disabled([max, format, extend]);
    } else if(data.value == 'Object') {
      // enabled(extend);
      // disabled([max, format]);
    } else if(data.value == 'Array') {
      // enabled(extend);
      // disabled([max, format]);
    }
    form.render();
  });

  // required
  form.on('switch(required)', function(data){
    if(data.elem.checked) {
      $(data.elem).prev().val(1);
    } else {
      $(data.elem).prev().val(0);
    }
  });

  // 初始化表单-主键
//  var item = $('#param-item').html().replace(new RegExp(/SERIAL/g), 1);
//  $('.param-form-item').append(item);
  form.render();
  // 初始化projectId
  $('input[name="project.id"]').val(projectId);
  // --------------------------------

  $('.create-form').on('click', function(){
    var s = $('#param-item').html();
    // 获取最后一个tr的序列号SERIAL
    var serial = $('.param-form-item tr:last').children().eq(1).html();
    if ( serial === undefined ) serial = 0;
    s = s.replace(new RegExp(/SERIAL/g), parseInt(serial) + 1);
    $('.param-form-item').append(s);
    form.render();
    return false;
  });

  $(document).on('click', '.delete-form', function(){
    var tr_list = $('.param-form-item').children();
//    if(tr_list.length == 1) {
//      layer.msg('没东西可删除了');
//      return false;
//    }
    $(this).parent().parent().remove();
    tr_list = $('.param-form-item').children();
    // 重构序列号
    for(var i = 1; i <= tr_list.length; i++) {
      var td = tr_list.eq(i - 1).children().eq(1);
      td.html(i);
    }
  });

  $('.format-json').on('click', function(e) {
    e.preventDefault();
    hideError();
    var json = $('textarea[name="interface.data"]').val();
    var format = null;
    try {
      format = parent.formatJson(json);
      $('textarea[name="interface.data"]').val(format.substr(1));
    } catch(e) {
      showError(e);
    }
    return false;
  });

  // 保存接口之前先格式化json
  var validateForm = function(json) {
    try {
      format = parent.formatJson(json);
      return true;
    } catch(e) {
      showError(e);
    }
    return false;
  }

  var showError = function(e) {
    $('.alert-danger').show();
    $('.alert-danger span').html('JSON格式错误[' + e + ']');
  }

  var hideError = function() {
    $('.alert-danger').hide();
  }

  // 拖拽事件
  $( ".param-form-item" ).sortable();
  $( ".param-form-item" ).draggable({
    axis: "y"
  });
  $( ".param-form-item tr" ).disableSelection();

  // 生成json模板的数据
  var temp_data = {
    'page_temp': '{\
                    "totalRow": 100,\
                    "totalPage": 10,\
                    "pageSize": 10,\
                    "pageNumber": 1,\
                    "list": [\
                    ]\
                  }'
  };
  // ----- 生成JSON 模拟数据模板代码
  $('.gen_json_temp').on('click', function() {
    var temp = $('select[name="select_json_temp"]').val();
    // 根据模板获取JSON数据
    $('textarea[name="interface.data"]').val(temp_data[temp]);
    return false;
  });

  // 生成返回值json模板的数据
  var result_temp_data = {
    'page_temp': '{\
                    "totalRow": "总行数",\
                    "totalPage": "总分页数",\
                    "pageSize": "每一页大小",\
                    "pageNumber": "当前分页索引",\
                    "list": [\
                    ]\
                  }'
  };
  // ----- 生成JSON 接口返回值配置模板代码
  $('.result_gen_json_temp').on('click', function() {
    var temp = $('select[name="result_select_json_temp"]').val();
    // 根据模板获取JSON数据
    $('textarea[name="interface.result_data"]').val(result_temp_data[temp]);
    return false;
  });

  $('.result_format_json').on('click', function(e) {
    e.preventDefault();
    hideError();
    var json = $('textarea[name="interface.result_data"]').val();
    var format = null;
    try {
      format = parent.formatJson(json);
      $('textarea[name="interface.result_data"]').val(format.substr(1));
    } catch(e) {
      showError(e);
    }
    return false;
  });
});