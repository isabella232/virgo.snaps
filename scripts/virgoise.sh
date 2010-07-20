#!/usr/bin/env bash
dir=`dirname $0`
dir=`cd $dir;pwd`

for f in $(find . -iname "*.java" -or -iname "*.aj" -or -iname "*.xml" -or -iname ".project" -or -iname "*.MF" -or -iname "*.mf" -or -iname ".prefs"  -or -iname ".classpath" -or -iname "build.versions" -or -iname "*.properties" -or -iname "*.bat" -or -iname "*.sh" -or -iname "*.conf" -or -iname "*.profile" -or -iname "*.config" -or -iname "*.plan" -or -iname "runner.bundles")
do
	
sed -i -e 's/SLICES_IVY_CACHE/SNAPS_IVY_CACHE/g' $f
sed -i -e 's/SLICE_/SNAP_/g' $f
sed -i -e 's/SLICES_/SNAPS_/g' $f

sed -i -e 's/com\.springsource\.osgi\.teststubs/org.eclipse.virgo.teststubs.osgi/g' $f
sed -i -e 's/com\.springsource\.osgi\.test\.stubs/org.eclipse.virgo.teststubs.osgi/g' $f

sed -i -e 's/com\.springsource\.osgi\.test\.framework/org.eclipse.virgo.test.framework/g' $f
sed -i -e 's/com\.springsource\.osgi\.test/org.eclipse.virgo.test/g' $f

sed -i -e 's/com\.springsource\.osgi\.webcontainer/org.eclipse.gemini.web/g' $f

sed -i -e 's/com\.springsource\.util/org.eclipse.virgo.util/g' $f

sed -i -e 's/com\.springsource\.osgi\.medic/org.eclipse.virgo.medic/g' $f
sed -i -e 's/com\.springsource\.osgi\.launcher/org.eclipse.virgo.osgi.launcher/g' $f

sed -i -e 's/com\.springsource\.kernel/org.eclipse.virgo.kernel/g' $f
sed -i -e 's/com\.springsource\.repository/org.eclipse.virgo.repository/g' $f

sed -i -e 's/com\.springsource\.server/org.eclipse.virgo/g' $f
sed -i -e 's/com\.springsource\.osgi/org.eclipse.virgo.osgi/g' $f

sed -i -e 's/com\.springsource\.ch\.qos\.logback\.classic\.woven/org.eclipse.virgo.ch.qos.logback.classic.woven/g' $f

# AspectJ update
sed -i -e 's/1\.6\.5\.RELEASE/1.6.6.RELEASE/g' $f
# JUnit update
sed -i -e 's/4\.5\.0/4.7.0/g' $f

rm $f-e
done

$dir/update-dependency.rb -v org.eclipse.virgo.teststubs.osgi=2.1.0.M01 build.versions
$dir/update-dependency.rb -v org.eclipse.virgo.test=2.1.0.M01 build.versions
$dir/update-dependency.rb -v org.eclipse.virgo.util=2.1.0.M01 build.versions
$dir/update-dependency.rb -v org.eclipse.virgo.web=2.1.0.M01 build.versions
$dir/update-dependency.rb -v org.eclipse.virgo.medic=2.1.0.M01 build.versions
$dir/update-dependency.rb -v org.eclipse.virgo.kernel=2.1.0.M01 build.version
$dir/update-dependency.rb -v org.eclipse.virgo.repository=2.1.0.M01 build.version

$dir/update-dependency.rb -v org.eclipse.gemini.web=1.1.0.M01 build.versions
