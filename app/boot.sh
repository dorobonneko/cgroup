path=`pm path com.moe.cgroup`
path=${path#package:}
killall Cgroupd
#export CLASSPATH=/storage/emulated/0/AppProjects/cgroup/app/classes.dex
app_process -Djava.class.path="$path" /system/bin --nice-name=Cgroupd  com.moe.cgroup.Cgroupd