<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="pl" xml:lang="pl">
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8" />
        <title>rcarousel - html content</title>
        <!--<link href='http://fonts.googleapis.com/css?family=Anton|Ubuntu' rel='stylesheet' type='text/css'>-->
        <link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/style/slider_carrusel/reset.css" />				
        <link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/style/slider_carrusel/style.css" />			
        <link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/style/slider_carrusel/rcarousel.css" />

        <style type="text/css">
            #container {
                width: 940px;
                position: relative;
                margin: 0 auto;
            }

            #carousel {
                width: 780px;
                margin: 0 auto;
            }

            #ui-carousel-next, #ui-carousel-prev {
                width: 60px;
                height: 240px;
                background: url(<%=request.getContextPath()%>/images/slider_carrusel/arrow-left.png) #fff center center no-repeat;
                display: block;
                position: absolute;
                top: 0;
                z-index: 100;
            }

            #ui-carousel-next {
                right: 0;
                background-image: url(<%=request.getContextPath()%>/images/slider_carrusel/arrow-right.png);
            }

            #ui-carousel-prev {
                left: 0;
            }

            #ui-carousel-next > span, #ui-carousel-prev > span {
                display: none;
            }

            .slide {
                margin: 0;
                position: relative;
            }

            .slide  h1 {
                font: 72px/1 Anton, sans-serif;
                color: #ff5c43;
                margin: 0;
                padding: 0;
            }

            .slide  p {
                font: 32px/1 Ubuntu, sans-serif;	
                color: #4d4d4d;
                margin: 0;
                padding: 0;
            }

            #slide01 > img {
                position: absolute;
                bottom: 35px;
                left: 30px;
            }

            #slide01 > .text {
                position: absolute;
                left: 290px;
                bottom: 35px;
            }

            #slide02 > img {
                position: absolute;
                bottom: 35px;
                left: 30px;
            }

            #slide02 > .text {
                position: absolute;
                left: 290px;
                bottom: 65px;
            }

            #slide03 > img {
                position: absolute;
                bottom: 25px;
                left: 30px;
            }

            #slide03 > .text {
                position: absolute;
                left: 270px;
                bottom: 25px;
            }

            #slide04 > img {
                position: absolute;
                bottom: 50px;
                left: 60px;
            }

            #slide04 > .text {
                position: absolute;
                left: 290px;
                bottom: 25px;
            }

            #slide05 > img {
                position: absolute;
                bottom: 35px;
                left: 60px;
            }

            #slide05 > .text {
                position: absolute;
                left: 240px;
                bottom: 35px;
            }

            #slide06 > img {
                position: absolute;
                bottom: 10px;
                left: 20px;
            }

            #slide06 > .text {
                position: absolute;
                left: 290px;
                bottom: 35px;
            }

            #pages {
                width: 150px;
                margin: 0 auto;
            }

            .bullet {
                background: url(<%=request.getContextPath()%>/images/slider_carrusel/page-off.png) center center no-repeat;
                display: block;
                width: 18px;
                height: 18px;
                margin: 0;
                margin-right: 5px;
                float: left;				
            }


        </style>
    </head>
    <body>
        <div id="header">
            <p>
                This is an example of <em>rcarousel</em> – a jQuery UI continuous carousel.
                Go back to the <a href="http://ryrych.github.com/rcarousel/">documentation</a>
            </p>
        </div>

        <div id="content">
            <h1>Any HTML element in slides</h1>

            <div id="container">
                <div id="carousel">
                    <div id="slide01" class="slide">
                        <img src="images/html_slides/jqueryui.png" alt="jQuery UI logo" />
                        <div class="text">
                            <h1>continuous<br />carousel</h1>
                            <p>driven by jQuery UI</p>
                        </div>
                    </div>

                    <div id="slide02" class="slide">
                        <img src="<%=request.getContextPath()%>/images/slider_carrusel/html_slides/anycontent.png" alt="any content" />
                        <div class="text">
                            <h1>any content</h1>
                            <p>from images to any HTML element</p>
                        </div>
                    </div>

                    <div id="slide03" class="slide">
                        <img src="<%=request.getContextPath()%>/images/slider_carrusel/html_slides/horizontalvertical.png" alt="horizontal and vertical carousel" />
                        <div class="text">
                            <h1>horizontal<br />and vertical</h1>
                            <p>carousels just one click away</p>
                        </div>
                    </div>

                    <div id="slide04" class="slide">
                        <img src="<%=request.getContextPath()%>/images/slider_carrusel/html_slides/multi.png" alt="multi carousels" />
                        <div class="text">
                            <h1>multi<br />carousels</h1>
                            <p>on a page</p>
                        </div>
                    </div>

                    <div id="slide05" class="slide">
                        <img src="<%=request.getContextPath()%>/images/slider_carrusel/html_slides/customization.png" alt="customization" />
                        <div class="text">
                            <h1>highly<br />customizable</h1>
                            <p>style it whatever you like</p>
                        </div>
                    </div>

                    <div id="slide06" class="slide">
                        <img src="<%=request.getContextPath()%>/images/slider_carrusel/html_slides/browsers.png" alt="multi browser support" />
                        <div class="text">
                            <h1>multi browser<br />support</h1>
                            <p>supports even old browsers</p>
                        </div>
                    </div>					
                </div>
                <a href="#" id="ui-carousel-next"><span>next</span></a>
                <a href="#" id="ui-carousel-prev"><span>prev</span></a>
                <div id="pages"></div>
            </div>
        </div>

        <script type="text/javascript" src="<%=request.getContextPath()%>/js/slider_carrusel/jquery-1.7.1.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/js/slider_carrusel/jquery.ui.core.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/js/slider_carrusel/jquery.ui.widget.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/js/slider_carrusel/jquery.ui.rcarousel.js"></script>
        <script type="text/javascript">
            jQuery(function($) {
                function generatePages() {
                    var _total, i, _link;

                    _total = $("#carousel").rcarousel("getTotalPages");

                    for (i = 0; i < _total; i++) {
                        _link = $("<a href='#'></a>");

                        $(_link)
                                .bind("click", {page: i},
                        function(event) {
                            $("#carousel").rcarousel("goToPage", event.data.page);
                            event.preventDefault();
                        }
                        )
                                .addClass("bullet off")
                                .appendTo("#pages");
                    }

                    // mark first page as active
                    $("a:eq(0)", "#pages")
                            .removeClass("off")
                                    .addClass("on")
                    .css( "background-image", "url(<%=request.getContextPath()%>/images/slider_carrusel/page-on.png)");
                }

                function pageLoaded(event, data) {
                    $("a.on", "#pages")
                                    .removeClass("on")
                            .css( "background-image", "url(<%=request.getContextPath()%>/images/slider_carrusel/page-off.png)");
                            $("a", "#pages")
                            .eq(data.page)
                                    .addClass("on")
                            .css( "background-image", "url(<%=request.getContextPath()%>/images/slider_carrusel/page-on.png)");
                }

                $("#carousel").rcarousel(
                        {
                            visible: 1,
                            step: 1,
                            speed: 700,
                            auto: {
                                enabled: true
                            },
                            width: 780,
                            height: 240,
                            start: generatePages,
                            pageLoaded: pageLoaded
                        }
                );

            });
        </script>
    </body>
</html>
