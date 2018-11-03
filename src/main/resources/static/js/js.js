$(function () {
    $("#list").click(function(){

    });

    $.ajax({
        url:"list",
        type:"get",
        // data:{username:username},
        success:function(data){
            // alert(data);
            $("#listDiv pre").text(data);
        },
        error:function(e){
            alert("错误！！");
        }
    });


})
