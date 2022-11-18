package net.francoiscolombo.tools.automaton.actions;

import net.francoiscolombo.tools.automaton.eval.EvalNumExpr;
import net.francoiscolombo.tools.automaton.exceptions.ParameterNotFound;
import oshi.PlatformEnum;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GetFacts extends AbstractAction {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    protected void execute() {
        this.exitCode = 1;
        if(SystemInfo.getCurrentPlatform() != PlatformEnum.UNKNOWN) {
            try {
                String factSet = getMandatoryParameter("set");

                LOGGER.info("Gathering facts...");
                SystemInfo si = new SystemInfo();
                HardwareAbstractionLayer hal = si.getHardware();
                OperatingSystem os = si.getOperatingSystem();

                LOGGER.info("Checking operating system...");
                gatherOperatingSystem(os);

                if("full".equalsIgnoreCase(factSet) || factSet.toLowerCase().contains("system")) {
                    LOGGER.info("Checking computer system...");
                    gatherComputerSystem(hal.getComputerSystem());
                }

                if("full".equalsIgnoreCase(factSet) || factSet.toLowerCase().contains("processor")) {
                    LOGGER.info("Checking Processor...");
                    gatherProcessor(hal.getProcessor());
                }

                if("full".equalsIgnoreCase(factSet) || factSet.toLowerCase().contains("memory")) {
                    LOGGER.info("Checking Memory...");
                    gatherMemory(hal.getMemory());
                }

                if("full".equalsIgnoreCase(factSet) || factSet.toLowerCase().contains("cpu")) {
                    LOGGER.info("Checking CPU...");
                    gatherCpu(hal.getProcessor());
                }

                if("full".equalsIgnoreCase(factSet) || factSet.toLowerCase().contains("processes")) {
                    LOGGER.info("Checking Processes...");
                    gatherProcesses(os, hal.getMemory());
                }

                if("full".equalsIgnoreCase(factSet) || factSet.toLowerCase().contains("services")) {
                    LOGGER.info("Checking Services...");
                    gatherServices(os);
                }

                if("full".equalsIgnoreCase(factSet) || factSet.toLowerCase().contains("sensors")) {
                    LOGGER.info("Checking Sensors...");
                    gatherSensors(hal.getSensors());
                }

                if("full".equalsIgnoreCase(factSet) || factSet.toLowerCase().contains("power")) {
                    LOGGER.info("Checking Power sources...");
                    gatherPowerSources(hal.getPowerSources());
                }

                if("full".equalsIgnoreCase(factSet) || factSet.toLowerCase().contains("disks")) {
                    LOGGER.info("Checking Disks...");
                    gatherDisks(hal.getDiskStores());

                    LOGGER.info("Checking Logical Volume Groups ...");
                    gatherLVgroups(hal.getLogicalVolumeGroups());
                }

                if("full".equalsIgnoreCase(factSet) || factSet.toLowerCase().contains("filesystem")) {
                    LOGGER.info("Checking File System...");
                    gatherFileSystem(os.getFileSystem());
                }

                if("full".equalsIgnoreCase(factSet) || factSet.toLowerCase().contains("network")) {
                    LOGGER.info("Checking Network interfaces...");
                    gatherNetworkInterfaces(hal.getNetworkIFs());

                    LOGGER.info("Checking Network parameters...");
                    gatherNetworkParameters(os.getNetworkParams());

                    LOGGER.info("Checking IP statistics...");
                    gatherInternetProtocolStats(os.getInternetProtocolStats());
                }

                if("full".equalsIgnoreCase(factSet) || factSet.toLowerCase().contains("displays")) {
                    LOGGER.info("Checking Displays...");
                    gatherDisplays(hal.getDisplays());
                }

                if("full".equalsIgnoreCase(factSet) || factSet.toLowerCase().contains("devices")) {
                    LOGGER.info("Checking USB Devices...");
                    gatherUsbDevices(hal.getUsbDevices(true));
                }

                if("full".equalsIgnoreCase(factSet) || factSet.toLowerCase().contains("cards")) {
                    LOGGER.info("Checking Sound Cards...");
                    gatherSoundCards(hal.getSoundCards());
                    LOGGER.info("Checking Graphics Cards...");
                    gatherGraphicsCards(hal.getGraphicsCards());
                }

                this.exitCode = 0;

            } catch (ParameterNotFound parameterNotFound) {
                LOGGER.warning(parameterNotFound.getMessage());
            }
        } else {
            LOGGER.warning("This platform is unknown, can't get the facts");
            this.exitCode = 2;
        }
    }

    private void gatherOperatingSystem(final OperatingSystem os) {
        setVariable("fact_os", String.valueOf(os));
        setVariable("fact_booted", Instant.ofEpochSecond(os.getSystemBootTime()));
        setVariable("fact_uptime", FormatUtil.formatElapsedSecs(os.getSystemUptime()));
        setVariable("fact_is_elevated", (os.isElevated() ? "yes" : "no"));
        for (OSSession s : os.getSessions()) {
            setVariable("fact_session_"+s.getUserName(), s.toString());
        }
    }

    private void gatherComputerSystem(final ComputerSystem computerSystem) {
        setVariable("fact_system", computerSystem.toString());
        setVariable(" fact_firmware", computerSystem.getFirmware().toString());
        setVariable(" fact_baseboard", computerSystem.getBaseboard().toString());
    }

    private void gatherProcessor(CentralProcessor processor) {
        setVariable("fact_processor", processor.toString());
        int maxEfficiency = 0;
        for (CentralProcessor.PhysicalProcessor cpu : processor.getPhysicalProcessors()) {
            int eff = cpu.getEfficiency();
            if (eff > maxEfficiency) {
                maxEfficiency = eff;
            }
        }
        for (CentralProcessor.PhysicalProcessor cpu : processor.getPhysicalProcessors()) {
            String logProc = processor.getLogicalProcessors().stream()
                    .filter(p -> p.getPhysicalProcessorNumber() == cpu.getPhysicalProcessorNumber())
                    .filter(p -> p.getPhysicalPackageNumber() == cpu.getPhysicalPackageNumber())
                    .map(p -> Integer.toString(p.getProcessorNumber())).collect(Collectors.joining(","));
            String pe = cpu.getEfficiency() == maxEfficiency ? "P" : "E";
            int proc = cpu.getPhysicalProcessorNumber();
            int pkg = cpu.getPhysicalPackageNumber();
            int numa = processor.getLogicalProcessors().stream()
                    .filter(p -> p.getPhysicalProcessorNumber() == cpu.getPhysicalProcessorNumber())
                    .filter(p -> p.getPhysicalPackageNumber() == cpu.getPhysicalPackageNumber())
                    .mapToInt(p -> p.getNumaNode()).findFirst().orElse(0);
            int pgrp = processor.getLogicalProcessors().stream()
                    .filter(p -> p.getPhysicalProcessorNumber() == cpu.getPhysicalProcessorNumber())
                    .filter(p -> p.getPhysicalPackageNumber() == cpu.getPhysicalPackageNumber())
                    .mapToInt(p -> p.getProcessorGroup()).findFirst().orElse(0);
            setVariable(
                    String.format("fact_proc_%d_topology", proc),
                    String.format("%7s %4s %4d %4d %4d", logProc, pe, pkg, numa, pgrp)
            );
        }
        List<CentralProcessor.ProcessorCache> caches = processor.getProcessorCaches();
        if (caches.isEmpty()) {
            setVariable("fact_proc_caches", "empty");
        } else {
            for (int i = 0; i < caches.size(); i++) {
                CentralProcessor.ProcessorCache cache = caches.get(i);
                boolean perCore = cache.getLevel() < 3;
                boolean pCore = perCore && i < caches.size() - 1 && cache.getLevel() == caches.get(i + 1).getLevel()
                        && cache.getType() == caches.get(i + 1).getType();
                boolean eCore = perCore && i > 0 && cache.getLevel() == caches.get(i - 1).getLevel()
                        && cache.getType() == caches.get(i - 1).getType();
                StringBuilder sb = new StringBuilder("  ").append(cache);
                if (perCore) {
                    sb.append(" (per ");
                    if (pCore) {
                        sb.append("P-");
                    } else if (eCore) {
                        sb.append("E-");
                    }
                    sb.append("core)");
                }
                setVariable("fact_proc_cache_"+i, sb.toString());
            }
        }
    }

    private void gatherMemory(GlobalMemory memory) {
        setVariable("fact_global_memory", memory.toString());
        VirtualMemory vm = memory.getVirtualMemory();
        setVariable("fact_virtual_memory", vm.toString());
        List<PhysicalMemory> pmList = memory.getPhysicalMemory();
        if (!pmList.isEmpty()) {
            setVariable("fact_physical_memory", String.valueOf(pmList));
        }
    }

    private void gatherCpu(CentralProcessor processor) {
        setVariable("fact_cpu_context_switches", processor.getContextSwitches());
        setVariable("fact_cpu_context_interrupts", processor.getInterrupts());
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
        // Wait a second...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long sys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;
        setVariable("fact_cpu_usage",
                String.format("User: %.1f%% Nice: %.1f%% System: %.1f%% Idle: %.1f%% IOwait: %.1f%% IRQ: %.1f%% SoftIRQ: %.1f%% Steal: %.1f%%",
                100d * user / totalCpu, 100d * nice / totalCpu, 100d * sys / totalCpu, 100d * idle / totalCpu,
                100d * iowait / totalCpu, 100d * irq / totalCpu, 100d * softirq / totalCpu, 100d * steal / totalCpu));
        setVariable("fact_cpu_load", String.format("CPU load: %.1f%%", processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100));
        double[] loadAverage = processor.getSystemLoadAverage(3);
        setVariable("fact_cpu_load_averages", (loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0]))
                + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2])));
        // per core CPU
        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
        double[] load = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
        for (double avg : load) {
            procCpu.append(String.format(" %.1f%%", avg * 100));
        }
        setVariable("fact_cpu_load_per_processor", procCpu.toString());
        long freq = processor.getProcessorIdentifier().getVendorFreq();
        if (freq > 0) {
            setVariable("fact_cpu_vendor_frequency", FormatUtil.formatHertz(freq));
        }
        freq = processor.getMaxFreq();
        if (freq > 0) {
            setVariable("fact_cpu_max_frequency", FormatUtil.formatHertz(freq));
        }
        long[] freqs = processor.getCurrentFreq();
        if (freqs[0] > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < freqs.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(FormatUtil.formatHertz(freqs[i]));
            }
            setVariable("fact_cpu_current_frequencies", sb.toString());
        }
    }

    private void gatherProcesses(OperatingSystem os, GlobalMemory memory) {
        OSProcess myProc = os.getProcess(os.getProcessId());
        // current process will never be null. Other code should check for null here
        setVariable("fact_proc_my_pid",myProc.getProcessID());
        setVariable("fact_proc_my_affinity", Long.toBinaryString(myProc.getAffinityMask()));
        setVariable("fact_process_count", os.getProcessCount());
        setVariable("fact_thread_count", os.getThreadCount());
        StringBuilder sb = new StringBuilder();
        for (String s : myProc.getArguments()) {
            sb.append(" ");
            sb.append(s);
        }
        setVariable("fact_proc_current_arguments", sb.toString().trim());
        // environment variables
        for (Map.Entry<String, String> e : myProc.getEnvironmentVariables().entrySet()) {
            setVariable("env_"+e.getKey(), e.getValue());
        }
        // Sort by highest CPU
        List<OSProcess> procs = os.getProcesses(OperatingSystem.ProcessFiltering.ALL_PROCESSES, OperatingSystem.ProcessSorting.CPU_DESC, 5);
        for (int i = 0; i < procs.size(); i++) {
            OSProcess p = procs.get(i);
            setVariable("fact_proc_pid_"+i, p.getProcessID());
            setVariable("fact_proc_cpu_percent_"+i, String.format("%5.1f", 100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime()));
            setVariable("fact_proc_mem_percent_"+i, String.format("%4.1f", 100d * (p.getResidentSetSize() / memory.getTotal())));
            setVariable("fact_proc_vsz_"+i, FormatUtil.formatBytes(p.getResidentSetSize()));
            setVariable("fact_proc_rss_name_"+i, p.getName());
        }
    }

    private void gatherServices(OperatingSystem os) {
        int i = 0;
        for (OSService s : os.getServices()) {
            setVariable("fact_service_"+i, s.toString());
            i++;
        }
    }

    private void gatherSensors(Sensors sensors) {
        setVariable("fact_sensors", sensors.toString());
    }

    private void gatherPowerSources(List<PowerSource> list) {
        StringBuilder sb = new StringBuilder();
        if (list.isEmpty()) {
            sb.append("Unknown");
        }
        for (PowerSource powerSource : list) {
            sb.append(" ").append(powerSource.toString());
        }
        setVariable("fact_power_sources", sb.toString().trim());
    }

    private void gatherDisks(List<HWDiskStore> list) {
        for (HWDiskStore disk : list) {
            setVariable("fact_disk_"+disk.getName(), disk.toString());
            List<HWPartition> partitions = disk.getPartitions();
            for (HWPartition part : partitions) {
                setVariable("fact_partition_"+part.getName(), part.toString());
            }
        }
    }

    private void gatherLVgroups(List<LogicalVolumeGroup> list) {
        if (!list.isEmpty()) {
            for (LogicalVolumeGroup lvg : list) {
                setVariable("fact_lv_"+lvg.getName(), lvg.toString());
            }
        }
    }

    private void gatherFileSystem(FileSystem fileSystem) {
        setVariable("fact_fs_open_fd", String.format("%d", fileSystem.getOpenFileDescriptors()));
        setVariable("fact_fs_max_fd", String.format("%d", fileSystem.getMaxFileDescriptors()));
        for (OSFileStore fs : fileSystem.getFileStores()) {
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            setVariable("fact_fs_"+fs.getName(),
                    String.format(
                    "%s (%s) [%s] %s of %s free (%.1f%%), %s of %s files free (%.1f%%) is %s "
                            + (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s")
                            + " and is mounted at %s",
                    fs.getName(), fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
                    FormatUtil.formatValue(fs.getFreeInodes(), ""), FormatUtil.formatValue(fs.getTotalInodes(), ""),
                    100d * fs.getFreeInodes() / fs.getTotalInodes(), fs.getVolume(), fs.getLogicalVolume(),
                    fs.getMount()));
        }
    }

    private void gatherNetworkInterfaces(List<NetworkIF> list) {
        if (!list.isEmpty()) {
            for (NetworkIF net : list) {
                setVariable("fact_net_"+net.getName(), net.toString());
            }
        }
    }

    private void gatherNetworkParameters(NetworkParams networkParams) {
        setVariable("fact_network_parameters", networkParams.toString());
    }

    private void gatherInternetProtocolStats(InternetProtocolStats ip) {
        setVariable("fact_tcpv4_statistics", ip.getTCPv4Stats());
        setVariable("fact_tcpv6_statistics", ip.getTCPv6Stats());
        setVariable("fact_udpv4_statistics", ip.getUDPv4Stats());
        setVariable("fact_udpv6_statistics", ip.getUDPv6Stats());
    }

    private void gatherDisplays(List<Display> list) {
        int i = 0;
        for (Display display : list) {
            setVariable("fact_display_" + i, String.valueOf(display));
            i++;
        }
    }

    private void gatherUsbDevices(List<UsbDevice> list) {
        for (UsbDevice usbDevice : list) {
            setVariable("fact_usb_"+usbDevice.getName(), String.valueOf(usbDevice));
        }
    }

    private void gatherSoundCards(List<SoundCard> list) {
        for (SoundCard card : list) {
            setVariable("fact_sound_card_"+card.getName(), String.valueOf(card));
        }
    }

    private void gatherGraphicsCards(List<GraphicsCard> list) {
        if (!list.isEmpty()) {
            for (GraphicsCard card : list) {
                setVariable("fact_graphic_card_"+card.getName(), String.valueOf(card));
            }
        }
    }
    
}
