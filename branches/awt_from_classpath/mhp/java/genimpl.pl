#!/usr/bin/perl

exit if ($ARGV[0] =~ /package\-.*.html/ or -d $ARGV[0]);
open(INPUT, "<$ARGV[0]") or die;
open(OUTPUT,">$ARGV[1]") or die;

foreach(<INPUT>) {
   $first=1 if ($_ =~ /START OF CLASS DATA/);
   $first=0 if ($_ =~ /INNER CLASS SUMMARY/);
   $field=1 if ($_ =~ /FIELD DETAIL/);
   $field=0 if ($_ =~ /CONSTRUCTOR DETAIL/);
   $constr=1 if ($_ =~ /CONSTRUCTOR DETAIL/);
   $constr=0 if ($_ =~ /METHOD DETAIL/);
   $method=1 if ($_ =~ /METHOD DETAIL/);
   $method=0 if ($_ =~ /END OF CLASS DATA/);
   
   $classn_raw.=$_ if ($first);
   $field_raw.=$_ if($field);
   $constr_raw.=$_ if ($constr);
   $method_raw.=$_ if ($method);
}

print "";
if ($classn_raw =~ /^\<DT\>(public (?:abstract )?(?:final )?(class|interface).*$)/m) {
      $class = $1;
      $interface= ( $2 =~ /interface/);
      $class =~ s/(\<.*?\>|\&nbsp\;)/ /g;
      $class =~ s/  / /g;
      $classn_raw =~ /\<P\>(.*?)\<P\>$/sm;
      $class_comment = $1;
      $classn_raw =~ /\<FONT SIZE\=\"\-1\"\>(.*?)\<\/FONT\>/sm;
      $package=$1;
      $package =~ s/\n//g;
      print OUTPUT "\npackage ".$package."\;\n\n/*\n".$class_comment."\n*/\n".$class."{\n\n";
      break;
} else {
   die "BUG! Did not find class name for $ARGV[0]";
}

while ($field_raw =~ /\<PRE\>(.*?)\<\/PRE\>/smgc) {
   $temp=$1; $temp=~ s/(\<.*?\>|\&nbsp\;)/ /g;$temp =~ s/  / /g;
   $fields[$#fields+1]=$temp;
   $field_raw =~ /(.*?)\n\n/smgc;
   $temp=$1; $temp=~ s/(\<.*?\>|\&nbsp\;)/ /g;$temp =~ s/  / /g;
   $fields_comments[$#fields_comments+1]=$temp;
}
 while ($constr_raw =~ /\<PRE\>(.*?)\<\/PRE\>/smgc) {
   $temp=$1; $temp=~ s/(\<.*?\>|\&nbsp\;)/ /g;$temp =~ s/  / /g;
   $members[$#members+1]=$temp;
   $constr_raw =~ /(.*?)\n\n/smgc;
   $temp=$1; $temp=~ s/(\<.*?\>|\&nbsp\;)/ /g;$temp =~ s/  / /g;
   $members_comments[$#members_comments+1]=$temp;
}
 while ($method_raw =~ /\<PRE\>(.*?)\<\/PRE\>/smgc) {
   $temp=$1; $temp=~ s/(\<.*?\>|\&nbsp\;)/ /g;$temp =~ s/  / /g;
   $members[$#members+1]=$temp;
   $method_raw =~ /(.*?)(?:\n\n|\z)/smgc;
   $temp=$1; $temp=~ s/(\<.*?\>|\&nbsp\;)/ /g;$temp =~ s/  / /g;
   $members_comments[$#members_comments+1]=$temp;
}

for ($i=0;$i<=$#fields;$i++) {
   print OUTPUT "/*".$fields_comments[$i]."*/\n".$fields[$i].";\n\n\n";
}
if ($interface) {
for ($i=0;$i<=$#members;$i++) {
   print OUTPUT "/*".$members_comments[$i]."*/\n".$members[$i].";\n\n\n";
}
} else {
for ($i=0;$i<=$#members;$i++) {
   print OUTPUT "/*".$members_comments[$i]."*/\n".$members[$i]."{\n}\n\n\n";
}
}

print OUTPUT "\n}\n\n";
 
   
 

exit;

   