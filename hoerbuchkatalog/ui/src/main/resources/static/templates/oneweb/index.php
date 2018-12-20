<?php defined('_JEXEC') or die;
/* =====================================================================
Template:	OneWeb for Joomla
Author: 	Seth Warburton - Internet Inspired! - @nternetinspired
Version: 	3.0
Created: 	April 2013
Copyright:	Seth Warburton - (C) 2013 - All rights reserved
Licenses:	GNU/GPL v3 or later http://www.gnu.org/licenses/gpl-3.0.html
            DBAD License http://philsturgeon.co.uk/code/dbad-license
/* ===================================================================== */

// Load template logic
include_once JPATH_THEMES . '/' . $this->template . '/logic.php';
?>
<!doctype html>
<head>
<meta http-equiv="Content-Language" content="de">
<jdoc:include type="head" />
	<link rel="stylesheet" href="<?php echo $this->baseurl ?>/templates/<?php echo $this->template;?>/css/position.css" type="text/css" media="screen,projection" />
	<link rel="stylesheet" href="<?php echo $this->baseurl ?>/templates/<?php echo $this->template;?>/css/layout.css" type="text/css" media="screen,projection" />
	<!-- <link rel="stylesheet" href="<?php echo $this->baseurl ?>/templates/<?php echo $this->template;?>/css/template.css" type="text/css" />   -->
    <!-- <link rel="stylesheet" href="<?php echo $this->baseurl ?>/templates/<?php echo $this->template;?>/css/print.css" type="text/css" media="Print" /> -->
	<!-- <link rel="stylesheet" href="<?php echo $this->baseurl ?>/templates/<?php echo $this->template;?>/css/general.css" type="text/css" /> -->

    <script type="text/javascript">
        var RecaptchaOptions = {
           lang : 'de', };
    </script>

</head>
    
<body class="<?php echo $siteHome ; ?>-page <?php echo $option . " view-" . $view . " itemid-" . $itemid . "";?>">
    <jdoc:include type="modules" name="cookie" style="xhtml" /> 
	<div class="header">
        <div class="wrapper">
			<header role="banner" class="content">
				<span class="unsichtbar">
                    <a href="#content" accesskey="2">zum Inhalt springen</a><br />
                    <a href="#topmenu" accesskey="3">zur Top-Navigation springen</a><br />
                    <a href="#mainmenu" accesskey="4">zum Hauptmenü springen</a><br />
                    <a href="#login" accesskey="5">zum Login</a><br />
                    <a href="#logout" accesskey="6">Abmelden/Ausloggen</a>

				</span>
				<div id="logoandfontsize">
					<div class="logo">
                        <h2 class="unsichtbar">WBH Logo</h2>
                        <jdoc:include type="modules" name="logo" style="gangnam" />
					</div>
					<div class="userlogout">
                        <a name="logout"></a>
						<h2 class="unsichtbar">Abmelden/Logout</h2>
						<jdoc:include type="modules" name="logout" style="gangnam" />
					</div>
				</div>
            </header>
        </div>
    </div>
	<div class="slider">
        <div class="wrapper">
    		<h2 class="unsichtbar">WBH Grafikbanner</h2>
    		<jdoc:include type="modules" name="fader" style="gangnam" />
    		<img src="../../images/stories/header-start_1.jpg" border="0" alt="Grafik Westdeutsche Blindenhörbücherei e.V., Münster"/>
        </div>
	</div>

    <div class="navigation">
        <div class="wrapper">
            <?php $user = JFactory::getUser(); ?>
            <?php if($user->id != 0) : ?>
                <div class="topmenu">
                    <a name="topmenu"></a>
                    <h2 class="unsichtbar">Top-Navigation</h2>                  
                    <jdoc:include type="modules" name="topmenu-login" style="gangnam" />
                </div>
            <?php endif; ?>
            <?php if($user->id == 0) : ?>
                <div class="topmenu">
                    <a name="topmenu"></a>
                    <h2 class="unsichtbar">Top-Navigation</h2>                  
                    <jdoc:include type="modules" name="topmenu" style="gangnam" />
                </div>
            <?php endif; ?>
        </div>
	</div><!-- navigation -->

	<div class="content">
        <div class="wrapper">
			<div id="left">
				<div id="left_inner">
					<h2 class="unsichtbar">WBH Suche</h2>
					<jdoc:include type="modules" name="leftsuchen" style="gangnam" />
					<a name="mainmenu"></a>
					<jdoc:include type="modules" name="left" style="gangnam" />
				</div>
			</div>

			<?php if ($this->countModules('right')) : ?>	
				<div id="maincontent">
					<div id="maincontent_inner">
						<a name="content"></a>
						<h2 class="unsichtbar">Inhalt</h2>
                        <div class="message">
                            <div class="wrapper">
                                <jdoc:include type="message" />
                            </div>
                        </div>
						<jdoc:include type="component" />
					</div>
				</div>
				<div id="right">
                    <div id="right_inner">
                        <a name="login"></a>
                        <h2 class="unsichtbar">Login</h2>
                        <jdoc:include type="modules" name="login" style="gangnam" />

                        <h2 class="unsichtbar">Rechte Spalte</h2>
                        <jdoc:include type="modules" name="right" style="gangnam" />
                    </div>
                </div>

			<?php else: ?>
				<div id="maincontent_full">
					<div id="maincontent_inner">
						<a name="content"></a>
						<h2 class="unsichtbar">Inhalt</h2>
                        <div class="message">
                            <div class="wrapper">
                                <jdoc:include type="message" />
                            </div>
                        </div>
						<jdoc:include type="component" />
					</div>
				</div>
			<?php endif; ?>
			<div class="wrap"></div>
        </div>
	</div><!-- end content -->

	<div id="footer">
		<h2 class="unsichtbar">Fusszeile</h2>
		<p class="syndicate">
			<jdoc:include type="modules" name="footer" />
		</p>
		<div class="wrap"></div>
	</div>



	<?php if($this->countModules('debug')) : ?>
		<jdoc:include type="modules" name="debug"/>
	<?php endif; ?>

	<?php if ($scripts > 0) : ?>
		<script src="<?php echo $this->baseurl ?>/templates/<?php echo $this->template ?>/js/scripts.min.js"></script>
		<script src="<?php echo $this->baseurl ?>/templates/<?php echo $this->template ?>/js/plugins.min.js"></script>
	<?php endif; ?>


    </body>
</html>
