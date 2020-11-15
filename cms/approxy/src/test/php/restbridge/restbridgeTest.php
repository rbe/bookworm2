<?php
/**
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

// Causes problems with other PHP code: declare(strict_types=1);

require_once '../../../../../vendor/phpunit/phpunit/src/Framework/TestCase.php';
require_once '../../../main/php/restbridge/restbridge.php';

use PHPUnit\Framework\TestCase;

class restbridgeTest extends TestCase
{


    /**
     * Test.
     *
     * @return void
     *
     * @since version
     */
    public function testOnContentPrepare(): void
    {
        $subject = 'subject';
        $params = [];
        $plg = new plgContentRestbridge($subject, $params);
        $context = '';
        $article = new MockArticle();
        $this->assertTrue($plg->onContentPrepare($context, $article, $params, 0));
        echo 'text: ' . $article->text;

    }//end testOnContentPrepare()


}//end class//end class
