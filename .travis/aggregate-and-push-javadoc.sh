#!/bin/sh

if [    "$TRAVIS_REPO_SLUG" = "zapodot/embedded-db-junit" -a \
        "$TRAVIS_PULL_REQUEST" = "false" -a \
        "$TRAVIS_BRANCH" = "master" ]
then
  mvn javadoc:aggregate-no-fork
  echo "Publishing javadoc..."

  cp -R target/site/apidocs $HOME/apidocs

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=master https://${GH_TOKEN}@github.com/zapodot/embedded-db-junit.git gh-pages > /dev/null

  cd gh-pages
  git rm -rf ./javadocs/latest
  cp -Rf $HOME/apidocs ./javadocs
  mv ./javadocs/apidocs ./javadocs/latest
  git add -f .
  git commit -m "Latest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin master > /dev/null

  echo "Published Javadoc to gh-pages."

fi
