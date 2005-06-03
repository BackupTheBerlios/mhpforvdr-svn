#!/usr/bin/perl

print "Package:";
$package=<STDIN>;
$package =~ s/\n$//;

print "Klasse:";
#while ( <STDIN> ) {
 #  if (/\+\+/) {
  #    goto cc;
   #}
   $class=<STDIN>;
#}
$class =~ s/\n$//;

@dirs=split(/\./, $package);
$class =~ /(class|interface) +([^ ]+)/ or die "could not parse class signature";
$classname=$2;
$isInterface = ( $1 =~ /interface/ );
system ("install", ("-d", join("/", @dirs)));
$filename=join("/", @dirs)."/".$classname.".java";
print "Saving to ",$filename,"\n";

print "Kommentar:";
#cc:
#while ( <STDIN> ) {
 #  if (/\+\-/) {
  #    goto mm;
   #}
   $class_comment=<STDIN>;
#}
$class_comment =~ s/\n$//;
$class_comment =~ s/(.{55,90} )/\1\n/g;

mm:
$index=0;
$which=1;
print "Funktionen:";
while ( <STDIN> ) {
   #if (/\+\+/) {
   #   $which=1;
   #} elsif (/\+\-/) {
   #*   $which=0;
   #  $index++;
   if (/ä/) {
      goto vv;
   } elsif (/ü/) {
      $which=1;
      next;
   }
   $eingabe=$_;
   $eingabe =~ s/(.{75,120} )/\1\n/g;
   $eingabe =~ s/\n$//;
   if ($which) {
      $members[$index]=$eingabe;
   } else {
      $members_comments[$index]=$eingabe;
      $index++;
      print "Jep!\n";
   }
   $which= (! $which);
}

vv:
print "Variablen:";
$index=0;
$which=1;
while ( <STDIN> ) {
   #if (/\+\+/) {
   #   $which=1;
   #} elsif (/\+\-/) {
   #   $which=0;
   #   $index++;
   if (/ä/) {
      schreib();
   } elsif (/ü/) {
      $which=1;
      next;
   }
   $eingabe=$_;
   $eingabe =~ s/(.{80,120} )/\1\n/g;
   $eingabe =~ s/\n$//;
   if ($which) {
      $vars[$index]=$eingabe;
   } else {
      $vars_comments[$index]=$eingabe;
      $index++;
      print "Jep!\n";
   }
   $which= (! $which);
}

schreib();

sub schreib {
   print "Danke.\n";
   #$filename="STDOUT";
   open(OUTPUT, ">".$filename) or die "Could not open file!!";
   print(OUTPUT "\npackage ".$package.";\n\n/*".$class_comment." */\n\n".$class." {\n\n");
   for ($i=0; $i<=$#vars;$i++) {
      print (OUTPUT "/*\n".$vars_comments[$i]." */\n".$vars[$i].";\n\n\n");
   }
   for ($i=0; $i<=$#members;$i++) {
      print (OUTPUT "/*\n".$members_comments[$i]." */\n".$members[$i].($isInterface ? ";\n\n\n" : " {\n}\n\n"));
   }
   print(OUTPUT "\n}\n");
   exit;
}
