require 'find'

class Version

  @@SEARCH_EXTENSIONS = [
    'classpath',
    'java',
    'properties',
    'versions',
    'xml'
  ]

  def self.update(variable, version, root)
    existing_version = nil
    IO.foreach(root + '/build.versions') do |line|
      existing_version = $1.strip if line =~ /#{variable}=(.*)/
    end

    if !existing_version.nil? && existing_version != version
      puts '    Updating ' + variable + ' from ' + existing_version + ' to ' + version
      Find.find(root) do |path|
        if FileTest.file?(path) && path !~ /ivy-cache|target/ && @@SEARCH_EXTENSIONS.include?(get_extension(path))
          lines = IO.readlines(path)
          changed = false
          lines.each do |line|
            if line =~ /#{variable}/
              changed = true if line.gsub!(/#{existing_version}/, version)
            end
          end

          if changed
            write_file(path, lines)
          end
        end
      end
    end
  end

########################################################################################################################

  private

  def self.get_extension(path)
    match_data = path.match('/.*\.(.*)')
    if match_data.nil?
      nil
    else
      match_data[1]
    end
  end

  def self.write_file(path, lines)
    file = File.new(path, 'w')
    lines.each do |line|
      file.write(line)
    end
    file.close
  end
  
end