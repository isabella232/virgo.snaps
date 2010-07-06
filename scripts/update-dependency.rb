#!/usr/bin/env ruby -wKU
$LOAD_PATH << File.expand_path(File.dirname(__FILE__))

require 'rubygems'
require 'choice'
require 'lib/version'

Choice.options do

  header('')
  header('Optional arguments:')

  option :versions, :required => false do
    short('-v')
    long('--version *VERSIONS')
    desc('A version to upgrade, in the form variable=version. Multiple versions may be specified')
  end

  option :file, :required => false do
    short('-f')
    long('--file')
    desc('A properties file containing variables and versions, in the from variable=version, to upgrade. A single file may be specified')
  end

end

args = Choice.choices

versions = Hash.new

if !args[:file].nil?
  IO.foreach(File.expand_path(args[:file])) do |line|
    versions[$1.strip] = $2.strip if line =~ /(.*)=(.*)/
  end
end

if !args[:versions].nil?
  args[:versions].each do |version|
    versions[$1.strip] = $2.strip if version =~ /(.*)=(.*)/
  end
end

versions.each do |variable, version|
  Version.update(variable, version, FileUtils.pwd)
end
