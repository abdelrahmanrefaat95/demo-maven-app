package com.example.demo;

import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@EnableConfigurationProperties(AppProperties.class)
public class AppController implements InfoContributor {
    private final AppProperties properties;
    private final ItemRepository items;
    private final BuildProperties build;
    private final Instant started = Instant.now();
    private final AtomicLong requests = new AtomicLong();

    public AppController(AppProperties properties, ItemRepository items, BuildProperties build) {
        this.properties = properties; this.items = items; this.build = build;
    }
    @GetMapping("/")
    public String page(Model model) { model.addAllAttributes(info()); model.addAttribute("requests", requests.incrementAndGet()); return "index"; }
    @GetMapping("/api/info") @ResponseBody
    public Map<String, Object> apiInfo() { return info(); }
    @GetMapping("/api/items") @ResponseBody
    public List<Item> listItems() { return items.findAll(); }
    @PostMapping("/api/items") @ResponseBody
    public Item addItem(@RequestBody ItemRequest request) { return items.save(new Item(request.name())); }
    private Map<String, Object> info() {
        return Map.of("version", build.getVersion(), "environment", properties.getEnvironment(), "color", properties.getColor(),
            "hostname", hostname(), "buildTime", build.getTime().toString(), "gitSha", gitSha(),
            "uptime", Duration.between(started, Instant.now()).toSeconds() + "s");
    }
    private String hostname() { try { return System.getenv().getOrDefault("HOSTNAME", InetAddress.getLocalHost().getHostName()); } catch (Exception e) { return "unknown"; } }
    private String gitSha() { String sha = build.get("gitSha"); return sha == null || "unavailable".equals(sha) ? "" : sha; }
    @Override public void contribute(org.springframework.boot.actuate.info.Info.Builder builder) { builder.withDetail("version", build.getVersion()).withDetail("gitSha", gitSha()); }
    public record ItemRequest(String name) { }
}
