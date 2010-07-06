#!/usr/bin/env ruby -wKU
$LOAD_PATH << File.expand_path(File.dirname(__FILE__))

require 'rubygems'
require 'choice'
require 'lib/version'
require 'ftools'
require 'rexml/document'
include REXML

def checkFileExists(filePath, filename) 
  if (!File.exist?(filePath)) then
    puts 'Cannot proceed: the \'' + filename + '\' file \'' + filePath + '\' does not exist.'
    Process.exit
  end
end

def updateEclipseClasspath(classpathFile, cacheVar, org, name, version)
  document = Document.new(File.new(classpathFile))
  classpath = document.root
    
  newEntry = Element.new('classpathentry')
  newEntry.attributes['kind'] = 'var'
  newEntry.attributes['sourcepath'] = '/' + cacheVar + '/' + org + '/' + name + '/' + version + '/' + name + '-sources-' + version + '.jar'
  newEntry.attributes['path'] = cacheVar + '/' + org + '/' + name + '/' + version + '/' + name + '-' + version + '.jar'
    
  classpath.add(newEntry)
    
  document.write(File.new(classpathFile, 'w'), 4)
end

def updateIvyDependencies(ivyFilePath, org, name, rev, conf)
  document = Document.new(File.new(ivyFilePath), { :raw => :all })
  dependencies = document.root.elements['dependencies']
    
  newDependency = Element.new('dependency')
  newDependency.add_attribute(Attribute.new('name', name))
  newDependency.add_attribute(Attribute.new('org', org))
  newDependency.add_attribute(Attribute.new('rev', '${' + rev + '}'))
  newDependency.add_attribute(Attribute.new('conf', conf))    
    
  dependencies.add(newDependency)
    
  updated = ''
  document.write(updated, 4)
  updated = updated.gsub('&gt;', '>')
    
  File.open(ivyFilePath, 'w') { |f|
    f.write(updated);
  }
end

def updateBuildVersions(buildVersionsPath, variable, version)
  versions = Hash.new
  IO.foreach(File.expand_path(buildVersionsPath)) do |line|
    versions[$1.strip] = $2.strip if line =~ /(.*)=(.*)/
  end
  if (!versions.key?(variable)) then
    File.open(buildVersionsPath, 'a') { |f|
      f.puts()
      f.puts(variable + '=' + version)
    }
  end
end

def getCurrentVersion(buildVersionsPath, variable, fallbackVersion)
  versions = Hash.new
  IO.foreach(File.expand_path(buildVersionsPath)) do |line|
    versions[$1.strip] = $2.strip if line =~ /(.*)=(.*)/
  end
  if (!versions.key?(variable)) then
    if (!fallbackVersion.nil?) then
      return fallbackVersion
    else
      puts 'ERROR -- build.versions does not specify a version for \'' + variable + '\' and you have not provided a version.'
      Process.exit
    end
  else
    if (!fallbackVersion.nil?) then
      puts 'WARNING -- build.versions already specifies the version \'' + versions[variable] + '\' for \'' + variable + '\'. Provided version \'' + fallbackVersion + '\' will be ignored.'
    end
    return versions[variable]
  end
end

Choice.options do

  header('')
  header('Required arguments:')

  option :org, :required => true do
    short('-o')
    long('--org ORG')
    desc('The org of the new dependency')
  end

  option :name, :required => true do
    short('-n')
    long('--name NAME')
    desc('The name of the new dependency')
  end
  
  option :ivycachevar, :required => true do
    short('-i')
    long('--ivycachevar IVYCACHEVAR')
    desc('The name of the Ivy cache variable used in Eclipse classpath')
  end
  
  separator('')
  separator('Optional arguments:')
  
  option :conf, :required => false do
    short('-c')
    long('--conf CONF')
    desc('The conf to be used in the Ivy dependency. Defaults to compile->compile')
    default('compile->compile')
  end
  
  option :rev, :rev => false do
    short('-r')
    long('--rev REV')
    desc('The value to use when forming the variable for the new Ivy dependency\'s rev attribute. Defaults to the provided org')
  end
  
  option :project, :required => false do
    short('-p')
    long('--project PROJECT')
    desc('The location of the project to which the dependency should be added. Defaults to the current directory')
    default('.')
  end
  
  option :buildversions, :required => false do
    short('-b')
    long('--buildversions BUILDVERSIONS')
    desc('The location of the build.versions file. Defaults to the current directory\'s parent')
    default('..')
  end
    
  option :version, :required => false do
    short('-v')
    long('--version VERSION')
    desc('The version of the new dependency. Defaults to the value specified in build.versions')
  end

end

args = Choice.choices

classpathFile = File.expand_path(args[:project] + '/.classpath')
checkFileExists(classpathFile, '.classpath')

ivyFile = File.expand_path(args[:project] + '/ivy.xml')
checkFileExists(ivyFile, 'ivy.xml')

buildVersionsFile = File.expand_path(args[:buildversions] + '/build.versions')
checkFileExists(buildVersionsFile, 'build.versions')

buildVersionVariable = (args[:rev] == nil ? args[:org] : args[:rev])

version = getCurrentVersion(buildVersionsFile, buildVersionVariable, args[:version])

updateEclipseClasspath(classpathFile, args[:ivycachevar], args[:org], args[:name], version)
updateIvyDependencies(ivyFile, args[:org], args[:name], buildVersionVariable, args[:conf])
updateBuildVersions(buildVersionsFile, buildVersionVariable, version)