/**
 * 一级评论提交回复
 */
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId, 1, content);
}

/**
 * 功能：提交回复处理函数
 * @param targetId
 * @param type
 * @param content
 */
function comment2target(targetId, type, content) {
    if(!content) {
        alert("不能回复空内容！");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: 'application/json',
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
                // $("#comment_section").hide();
                // console.log(response);
            } else {
                if (response.code == 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=8941f94d216cfea4582c&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", true);
                    }
                } else {
                    alert(response.message);
                }
                console.log(response);
            }
        },
        dataType: "json"
    });
}

/**
 * 二级评论提交回复
 */
function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();
    comment2target(commentId, 2, content);
}

/**
 * 展开二级评论
 */
function collapseComments(e) {
    var id = e.getAttribute("data-id"); // 一级评论的id
    var comments = $("#comment-" + id);

    // 获取一下二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        // 折叠二级评论
        comments.removeClass("in"); // collapse是折叠二级评论，collapse in是展开二级评论
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        var subCommentContainer = $("#comment-" + id);
        if(subCommentContainer.children().length != 1) {
            // 展开二级评论
            comments.addClass("in");
            // 标记二级评论展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active"); // 使用css控制展开按钮变成蓝色
        } else {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })));

                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });

                // 展开二级评论
                comments.addClass("in");
                // 标记二级评论展开状态
                e.setAttribute("data-collapse", "in");
                e.classList.add("active"); // 使用css控制展开按钮变成蓝色
            });
        }
    }
}

/**
 * 让原本隐藏的标签库列表显示出来
 */
function showSelectTag() {
    $("#select-tag").show();
}

/**
 * 选择标签并将其添加到标签控件中
 */
function selectTag(e) {
    var value = e.getAttribute("data-tag"); // value是选中的标签字符串值
    var previous = $("#tag").val(); // previous是当前已有的标签值
    if (previous.indexOf(value) == -1) { // 判断当前选中的值是否已经存在于选中的标签集合中
        if (previous) { // 判断先前是否已经存在标签值
            $("#tag").val(previous + ',' + value); // 如果存在就叠加值
        } else {
            $("#tag").val(value); // 如果不存在直接写入
        }
    }
}
