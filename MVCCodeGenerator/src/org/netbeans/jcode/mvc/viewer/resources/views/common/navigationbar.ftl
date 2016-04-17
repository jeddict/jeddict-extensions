<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- Navigation -->
<nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
    <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="http://jpamodeler.github.io" target="_blank"><img src="${webPath}/static/images/logo.png" /></a>
    </div>
    <!-- /.navbar-header -->

    <ul class="nav navbar-top-links navbar-right">
        <li><a href="https://twitter.com/JPAModeler" target="_blank"><i class="fa fa-twitter"></i></a></li>
        <li><a href="https://youtube.com/JPAModeler" target="_blank"><i class="fa fa-youtube"></i></a></li>
        <li><a href="https://github.com/jGauravGupta/jpamodeler" target="_blank"><i class="fa fa-github"></i></a></li>
        <li><a href="https://twitter.com/intent/tweet?text=%40JPAModeler%20%3C3" target="_blank"><i class="fa fa-heart"></i></a></li>
        <li class="dropdown">
            <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                <i class="fa fa-envelope fa-fw"></i>  <i class="fa fa-caret-down"></i>
            </a>
            <ul class="dropdown-menu dropdown-messages">
                <li>
                    <a href="http://jpamodeler.github.io/tutorial/page.html?l=QuickStart" target="_blank">
                        <div>
                            <strong>Quick Start</strong>
                            <span class="pull-right text-muted">
                                <em><i class="fa fa-info-circle fa-fw"></i></em>
                            </span>
                        </div>
                        <div>Quick introduction to the JPA Modeler workflow by walking you through the creation of a simple JPA application...</div>
                    </a>
                </li>
                <li class="divider"></li>
                <li>
                    <a class="text-center" href="http://jpamodeler.github.io" target="_blank">
                        <strong>Learn other tutorial</strong>
                        <i class="fa fa-angle-right"></i>
                    </a>
                </li>
            </ul>
            <!-- /.dropdown-messages -->
        </li>
        <!-- /.dropdown -->
    </ul>
    <!-- /.navbar-top-links -->

    <div class="navbar-default sidebar" role="navigation">
        <div class="sidebar-nav navbar-collapse">
            <ul class="nav" id="side-menu">
                <li>
                    <a href="${webPath}/index.jsp"><i class="fa fa-dashboard fa-fw"></i> Dashboard</a>
                </li>
                <li>
                    <a href="https://blogs.oracle.com/theaquarium/entry/why_another_mvc_framework_in"  target="_blank"><i class="fa fa-question-circle fa-fw"></i>Why Another MVC?</a>
                </li>
                <li>
                    <a href="#"><i class="fa fa-info-circle fa-fw"></i> MVC 1.0 Info<span class="fa arrow"></span></a>
                    <ul class="nav nav-second-level">
                        <li>
                            <a href="https://www.jcp.org/en/jsr/detail?id=371"  target="_blank">MVC 1.0 - JSR 371</a>
                        </li>
                        <li>
                            <a href="https://ozark.java.net/"  target="_blank">MVC RI</a>
                        </li>
                        <li>
                            <a href="https://java.net/projects/mvc-spec/pages/Home"  target="_blank">MVC Project</a>
                        </li>
                    </ul>
                    <!-- /.nav-second-level -->
                </li>
            </ul>
        </div>
        <!-- /.sidebar-collapse -->
    </div>
    <!-- /.navbar-static-side -->
</nav>
