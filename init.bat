::
:: Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
:: Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
:: All rights reserved. Use is subject to license terms.
::

set PROJECT=~/project/wbh.bookworm
set REPO=artofcoding/bookworm2.git
set BRANCH=develop

git clone -b %BRANCH% git@bitbucket.org:%REPO% %PROJECT%
cd %PROJECT%
.\mvnw.cmd clean
