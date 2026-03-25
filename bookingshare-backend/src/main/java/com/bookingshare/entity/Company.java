package com.bookingshare.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String email;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BusinessHoursPattern> patterns = new HashSet<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExceptionOverride> exceptions = new HashSet<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SlotConfiguration> configurations = new HashSet<>();

    public Company() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<BusinessHoursPattern> getPatterns() { return patterns; }
    public void setPatterns(Set<BusinessHoursPattern> patterns) { this.patterns = patterns; }

    public Set<ExceptionOverride> getExceptions() { return exceptions; }
    public void setExceptions(Set<ExceptionOverride> exceptions) { this.exceptions = exceptions; }

    public Set<SlotConfiguration> getConfigurations() { return configurations; }
    public void setConfigurations(Set<SlotConfiguration> configurations) { this.configurations = configurations; }
}
