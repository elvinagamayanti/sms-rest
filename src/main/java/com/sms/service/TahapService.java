package com.sms.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sms.dto.TahapStatusDto;
import com.sms.entity.Kegiatan;
import com.sms.entity.Tahap1;
import com.sms.entity.Tahap2;
import com.sms.entity.Tahap3;
import com.sms.entity.Tahap4;
import com.sms.entity.Tahap5;
import com.sms.entity.Tahap6;
import com.sms.entity.Tahap7;
import com.sms.entity.Tahap8;
import com.sms.repository.Tahap1Repository;
import com.sms.repository.Tahap2Repository;
import com.sms.repository.Tahap3Repository;
import com.sms.repository.Tahap4Repository;
import com.sms.repository.Tahap5Repository;
import com.sms.repository.Tahap6Repository;
import com.sms.repository.Tahap7Repository;
import com.sms.repository.Tahap8Repository;

@Service
public class TahapService {
    private final Tahap1Repository tahap1Repository;
    private final Tahap2Repository tahap2Repository;
    private final Tahap3Repository tahap3Repository;
    private final Tahap4Repository tahap4Repository;
    private final Tahap5Repository tahap5Repository;
    private final Tahap6Repository tahap6Repository;
    private final Tahap7Repository tahap7Repository;
    private final Tahap8Repository tahap8Repository;
    private final FileUploadService fileUploadService;

    public TahapService(Tahap1Repository tahap1Repository, Tahap2Repository tahap2Repository,
            Tahap3Repository tahap3Repository, Tahap4Repository tahap4Repository,
            Tahap5Repository tahap5Repository, Tahap6Repository tahap6Repository,
            Tahap7Repository tahap7Repository, Tahap8Repository tahap8Repository, FileUploadService fileUploadService) {
        this.tahap1Repository = tahap1Repository;
        this.tahap2Repository = tahap2Repository;
        this.tahap3Repository = tahap3Repository;
        this.tahap4Repository = tahap4Repository;
        this.tahap5Repository = tahap5Repository;
        this.tahap6Repository = tahap6Repository;
        this.tahap7Repository = tahap7Repository;
        this.tahap8Repository = tahap8Repository;
        this.fileUploadService = fileUploadService;
    }

    // Method to check if a specific subtask is completed
    public boolean isSubtaskCompleted(Long kegiatanId, int tahap, int subtahap) {
        switch (tahap) {
            case 1 -> {
                return isTahap1SubtaskCompleted(kegiatanId, subtahap);
            }
            case 2 -> {
                return isTahap2SubtaskCompleted(kegiatanId, subtahap);
            }
            case 3 -> {
                return isTahap3SubtaskCompleted(kegiatanId, subtahap);
            }
            case 4 -> {
                return isTahap4SubtaskCompleted(kegiatanId, subtahap);
            }
            case 5 -> {
                return isTahap5SubtaskCompleted(kegiatanId, subtahap);
            }
            case 6 -> {
                return isTahap6SubtaskCompleted(kegiatanId, subtahap);
            }
            case 7 -> {
                return isTahap7SubtaskCompleted(kegiatanId, subtahap);
            }
            case 8 -> {
                return isTahap8SubtaskCompleted(kegiatanId, subtahap);
            }
            default -> throw new IllegalArgumentException("Invalid tahap: " + tahap);
        }
    }

    private boolean isTahap1SubtaskCompleted(Long kegiatanId, int subtahap) {
        Optional<Tahap1> tahap1Opt = tahap1Repository.findByKegiatanId(kegiatanId);
        if (tahap1Opt.isEmpty()) {
            return false;
        }

        Tahap1 tahap1 = tahap1Opt.get();
        switch (subtahap) {
            case 1 -> {
                return tahap1.isSubtahap_1();
            }
            case 2 -> {
                return tahap1.isSubtahap_2();
            }
            case 3 -> {
                return tahap1.isSubtahap_3();
            }
            case 4 -> {
                return tahap1.isSubtahap_4();
            }
            case 5 -> {
                return tahap1.isSubtahap_5();
            }
            case 6 -> {
                return tahap1.isSubtahap_6();
            }
            default -> throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }
    }

    private boolean isTahap2SubtaskCompleted(Long kegiatanId, int subtahap) {
        Optional<Tahap2> tahap2Opt = tahap2Repository.findByKegiatanId(kegiatanId);
        if (tahap2Opt.isEmpty()) {
            return false;
        }

        Tahap2 tahap2 = tahap2Opt.get();
        switch (subtahap) {
            case 1 -> {
                return tahap2.isSubtahap_1();
            }
            case 2 -> {
                return tahap2.isSubtahap_2();
            }
            case 3 -> {
                return tahap2.isSubtahap_3();
            }
            case 4 -> {
                return tahap2.isSubtahap_4();
            }
            default -> throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }
    }

    private boolean isTahap3SubtaskCompleted(Long kegiatanId, int subtahap) {
        Optional<Tahap3> tahap3Opt = tahap3Repository.findByKegiatanId(kegiatanId);
        if (tahap3Opt.isEmpty()) {
            return false;
        }

        Tahap3 tahap3 = tahap3Opt.get();
        switch (subtahap) {
            case 1 -> {
                return tahap3.isSubtahap_1();
            }
            case 2 -> {
                return tahap3.isSubtahap_2();
            }
            case 3 -> {
                return tahap3.isSubtahap_3();
            }
            case 4 -> {
                return tahap3.isSubtahap_4();
            }
            default -> throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }
    }

    private boolean isTahap4SubtaskCompleted(Long kegiatanId, int subtahap) {
        Optional<Tahap4> tahap4Opt = tahap4Repository.findByKegiatanId(kegiatanId);
        if (tahap4Opt.isEmpty()) {
            return false;
        }

        Tahap4 tahap4 = tahap4Opt.get();
        switch (subtahap) {
            case 1 -> {
                return tahap4.isSubtahap_1();
            }
            case 2 -> {
                return tahap4.isSubtahap_2();
            }
            case 3 -> {
                return tahap4.isSubtahap_3();
            }
            case 4 -> {
                return tahap4.isSubtahap_4();
            }
            default -> throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }
    }

    private boolean isTahap5SubtaskCompleted(Long kegiatanId, int subtahap) {
        Optional<Tahap5> tahap5Opt = tahap5Repository.findByKegiatanId(kegiatanId);
        if (tahap5Opt.isEmpty()) {
            return false;
        }

        Tahap5 tahap5 = tahap5Opt.get();
        switch (subtahap) {
            case 1 -> {
                return tahap5.isSubtahap_1();
            }
            case 2 -> {
                return tahap5.isSubtahap_2();
            }
            case 3 -> {
                return tahap5.isSubtahap_3();
            }
            case 4 -> {
                return tahap5.isSubtahap_4();
            }
            default -> throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }
    }

    private boolean isTahap6SubtaskCompleted(Long kegiatanId, int subtahap) {
        Optional<Tahap6> tahap6Opt = tahap6Repository.findByKegiatanId(kegiatanId);
        if (tahap6Opt.isEmpty()) {
            return false;
        }

        Tahap6 tahap6 = tahap6Opt.get();
        switch (subtahap) {
            case 1 -> {
                return tahap6.isSubtahap_1();
            }
            case 2 -> {
                return tahap6.isSubtahap_2();
            }
            case 3 -> {
                return tahap6.isSubtahap_3();
            }
            case 4 -> {
                return tahap6.isSubtahap_4();
            }
            default -> throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }
    }

    private boolean isTahap7SubtaskCompleted(Long kegiatanId, int subtahap) {
        Optional<Tahap7> tahap7Opt = tahap7Repository.findByKegiatanId(kegiatanId);
        if (tahap7Opt.isEmpty()) {
            return false;
        }

        Tahap7 tahap7 = tahap7Opt.get();
        switch (subtahap) {
            case 1 -> {
                return tahap7.isSubtahap_1();
            }
            case 2 -> {
                return tahap7.isSubtahap_2();
            }
            case 3 -> {
                return tahap7.isSubtahap_3();
            }
            case 4 -> {
                return tahap7.isSubtahap_4();
            }
            default -> throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }
    }

    private boolean isTahap8SubtaskCompleted(Long kegiatanId, int subtahap) {
        Optional<Tahap8> tahap8Opt = tahap8Repository.findByKegiatanId(kegiatanId);
        if (tahap8Opt.isEmpty()) {
            return false;
        }

        Tahap8 tahap8 = tahap8Opt.get();
        switch (subtahap) {
            case 1 -> {
                return tahap8.isSubtahap_1();
            }
            case 2 -> {
                return tahap8.isSubtahap_2();
            }
            case 3 -> {
                return tahap8.isSubtahap_3();
            }
            case 4 -> {
                return tahap8.isSubtahap_4();
            }
            default -> throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }
    }

    // Method to update subtask completion status
    public void updateSubtaskStatus(Long kegiatanId, int tahap, int subtahap, boolean completed) {
        switch (tahap) {
            case 1:
                updateTahap1SubtaskStatus(kegiatanId, subtahap, completed);
                break;
            case 2:
                updateTahap2SubtaskStatus(kegiatanId, subtahap, completed);
                break;
            case 3:
                updateTahap3SubtaskStatus(kegiatanId, subtahap, completed);
                break;
            case 4:
                updateTahap4SubtaskStatus(kegiatanId, subtahap, completed);
                break;
            case 5:
                updateTahap5SubtaskStatus(kegiatanId, subtahap, completed);
                break;
            case 6:
                updateTahap6SubtaskStatus(kegiatanId, subtahap, completed);
                break;
            case 7:
                updateTahap7SubtaskStatus(kegiatanId, subtahap, completed);
                break;
            case 8:
                updateTahap8SubtaskStatus(kegiatanId, subtahap, completed);
                break;
            default:
                throw new IllegalArgumentException("Invalid tahap: " + tahap);
        }
    }

    private void updateTahap1SubtaskStatus(Long kegiatanId, int subtahap, boolean completed) {
        Tahap1 tahap1 = tahap1Repository.findByKegiatanId(kegiatanId)
                .orElseGet(() -> {
                    Kegiatan kegiatan = new Kegiatan();
                    kegiatan.setId(kegiatanId);

                    Tahap1 newTahap = new Tahap1();
                    newTahap.setKegiatan(kegiatan);
                    return newTahap;
                });

        switch (subtahap) {
            case 1:
                tahap1.setSubtahap_1(completed);
                break;
            case 2:
                tahap1.setSubtahap_2(completed);
                break;
            case 3:
                tahap1.setSubtahap_3(completed);
                break;
            case 4:
                tahap1.setSubtahap_4(completed);
                break;
            case 5:
                tahap1.setSubtahap_5(completed);
                break;
            case 6:
                tahap1.setSubtahap_6(completed);
                break;
            default:
                throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }

        tahap1Repository.save(tahap1);
    }

    private void updateTahap2SubtaskStatus(Long kegiatanId, int subtahap, boolean completed) {
        Tahap2 tahap2 = tahap2Repository.findByKegiatanId(kegiatanId)
                .orElseGet(() -> {
                    Kegiatan kegiatan = new Kegiatan();
                    kegiatan.setId(kegiatanId);

                    Tahap2 newTahap = new Tahap2();
                    newTahap.setKegiatan(kegiatan);
                    return newTahap;
                });

        switch (subtahap) {
            case 1:
                tahap2.setSubtahap_1(completed);
                break;
            case 2:
                tahap2.setSubtahap_2(completed);
                break;
            case 3:
                tahap2.setSubtahap_3(completed);
                break;
            case 4:
                tahap2.setSubtahap_4(completed);
                break;
            default:
                throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }

        tahap2Repository.save(tahap2);
    }

    private void updateTahap3SubtaskStatus(Long kegiatanId, int subtahap, boolean completed) {
        Tahap3 tahap3 = tahap3Repository.findByKegiatanId(kegiatanId)
                .orElseGet(() -> {
                    Kegiatan kegiatan = new Kegiatan();
                    kegiatan.setId(kegiatanId);

                    Tahap3 newTahap = new Tahap3();
                    newTahap.setKegiatan(kegiatan);
                    return newTahap;
                });

        switch (subtahap) {
            case 1:
                tahap3.setSubtahap_1(completed);
                break;
            case 2:
                tahap3.setSubtahap_2(completed);
                break;
            case 3:
                tahap3.setSubtahap_3(completed);
                break;
            case 4:
                tahap3.setSubtahap_4(completed);
                break;
            default:
                throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }

        tahap3Repository.save(tahap3);
    }

    private void updateTahap4SubtaskStatus(Long kegiatanId, int subtahap, boolean completed) {
        Tahap4 tahap4 = tahap4Repository.findByKegiatanId(kegiatanId)
                .orElseGet(() -> {
                    Kegiatan kegiatan = new Kegiatan();
                    kegiatan.setId(kegiatanId);

                    Tahap4 newTahap = new Tahap4();
                    newTahap.setKegiatan(kegiatan);
                    return newTahap;
                });

        switch (subtahap) {
            case 1:
                tahap4.setSubtahap_1(completed);
                break;
            case 2:
                tahap4.setSubtahap_2(completed);
                break;
            case 3:
                tahap4.setSubtahap_3(completed);
                break;
            case 4:
                tahap4.setSubtahap_4(completed);
                break;
            default:
                throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }

        tahap4Repository.save(tahap4);
    }

    private void updateTahap5SubtaskStatus(Long kegiatanId, int subtahap, boolean completed) {
        Tahap5 tahap5 = tahap5Repository.findByKegiatanId(kegiatanId)
                .orElseGet(() -> {
                    Kegiatan kegiatan = new Kegiatan();
                    kegiatan.setId(kegiatanId);

                    Tahap5 newTahap = new Tahap5();
                    newTahap.setKegiatan(kegiatan);
                    return newTahap;
                });

        switch (subtahap) {
            case 1:
                tahap5.setSubtahap_1(completed);
                break;
            case 2:
                tahap5.setSubtahap_2(completed);
                break;
            case 3:
                tahap5.setSubtahap_3(completed);
                break;
            case 4:
                tahap5.setSubtahap_4(completed);
                break;
            default:
                throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }

        tahap5Repository.save(tahap5);
    }

    private void updateTahap6SubtaskStatus(Long kegiatanId, int subtahap, boolean completed) {
        Tahap6 tahap6 = tahap6Repository.findByKegiatanId(kegiatanId)
                .orElseGet(() -> {
                    Kegiatan kegiatan = new Kegiatan();
                    kegiatan.setId(kegiatanId);

                    Tahap6 newTahap = new Tahap6();
                    newTahap.setKegiatan(kegiatan);
                    return newTahap;
                });

        switch (subtahap) {
            case 1:
                tahap6.setSubtahap_1(completed);
                break;
            case 2:
                tahap6.setSubtahap_2(completed);
                break;
            case 3:
                tahap6.setSubtahap_3(completed);
                break;
            case 4:
                tahap6.setSubtahap_4(completed);
                break;
            default:
                throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }

        tahap6Repository.save(tahap6);
    }

    private void updateTahap7SubtaskStatus(Long kegiatanId, int subtahap, boolean completed) {
        Tahap7 tahap7 = tahap7Repository.findByKegiatanId(kegiatanId)
                .orElseGet(() -> {
                    Kegiatan kegiatan = new Kegiatan();
                    kegiatan.setId(kegiatanId);

                    Tahap7 newTahap = new Tahap7();
                    newTahap.setKegiatan(kegiatan);
                    return newTahap;
                });

        switch (subtahap) {
            case 1:
                tahap7.setSubtahap_1(completed);
                break;
            case 2:
                tahap7.setSubtahap_2(completed);
                break;
            case 3:
                tahap7.setSubtahap_3(completed);
                break;
            case 4:
                tahap7.setSubtahap_4(completed);
                break;
            default:
                throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }

        tahap7Repository.save(tahap7);
    }

    private void updateTahap8SubtaskStatus(Long kegiatanId, int subtahap, boolean completed) {
        Tahap8 tahap8 = tahap8Repository.findByKegiatanId(kegiatanId)
                .orElseGet(() -> {
                    Kegiatan kegiatan = new Kegiatan();
                    kegiatan.setId(kegiatanId);

                    Tahap8 newTahap = new Tahap8();
                    newTahap.setKegiatan(kegiatan);
                    return newTahap;
                });

        switch (subtahap) {
            case 1:
                tahap8.setSubtahap_1(completed);
                break;
            case 2:
                tahap8.setSubtahap_2(completed);
                break;
            case 3:
                tahap8.setSubtahap_3(completed);
                break;
            case 4:
                tahap8.setSubtahap_4(completed);
                break;
            default:
                throw new IllegalArgumentException("Invalid subtahap: " + subtahap);
        }

        tahap8Repository.save(tahap8);
    }

    // Get overall GSBPM phase completion percentage for a kegiatan
    public int getTahapCompletionPercentage(Long kegiatanId, int tahap) {
        switch (tahap) {
            case 1:
                Optional<Tahap1> tahap1 = tahap1Repository.findByKegiatanId(kegiatanId);
                return tahap1.map(Tahap1::getCompletionPercentage).orElse(0);
            case 2:
                Optional<Tahap2> tahap2 = tahap2Repository.findByKegiatanId(kegiatanId);
                return tahap2.map(Tahap2::getCompletionPercentage).orElse(0);
            case 3:
                Optional<Tahap3> tahap3 = tahap3Repository.findByKegiatanId(kegiatanId);
                return tahap3.map(Tahap3::getCompletionPercentage).orElse(0);
            case 4:
                Optional<Tahap4> tahap4 = tahap4Repository.findByKegiatanId(kegiatanId);
                return tahap4.map(Tahap4::getCompletionPercentage).orElse(0);
            case 5:
                Optional<Tahap5> tahap5 = tahap5Repository.findByKegiatanId(kegiatanId);
                return tahap5.map(Tahap5::getCompletionPercentage).orElse(0);
            case 6:
                Optional<Tahap6> tahap6 = tahap6Repository.findByKegiatanId(kegiatanId);
                return tahap6.map(Tahap6::getCompletionPercentage).orElse(0);
            case 7:
                Optional<Tahap7> tahap7 = tahap7Repository.findByKegiatanId(kegiatanId);
                return tahap7.map(Tahap7::getCompletionPercentage).orElse(0);
            case 8:
                Optional<Tahap8> tahap8 = tahap8Repository.findByKegiatanId(kegiatanId);
                return tahap8.map(Tahap8::getCompletionPercentage).orElse(0);
            default:
                throw new IllegalArgumentException("Invalid tahap: " + tahap);
        }
    }

    // Get all tahap data for a kegiatan
    public TahapStatusDto getTahapStatus(Long kegiatanId) {
        TahapStatusDto status = new TahapStatusDto();
        status.setKegiatanId(kegiatanId);

        Optional<Tahap1> tahap1 = tahap1Repository.findByKegiatanId(kegiatanId);
        status.setTahap1(tahap1.orElse(new Tahap1()));
        status.setTahap1Percentage(tahap1.map(Tahap1::getCompletionPercentage).orElse(0));

        Optional<Tahap2> tahap2 = tahap2Repository.findByKegiatanId(kegiatanId);
        status.setTahap2(tahap2.orElse(new Tahap2()));
        status.setTahap2Percentage(tahap2.map(Tahap2::getCompletionPercentage).orElse(0));

        Optional<Tahap3> tahap3 = tahap3Repository.findByKegiatanId(kegiatanId);
        status.setTahap3(tahap3.orElse(new Tahap3()));
        status.setTahap3Percentage(tahap3.map(Tahap3::getCompletionPercentage).orElse(0));

        Optional<Tahap4> tahap4 = tahap4Repository.findByKegiatanId(kegiatanId);
        status.setTahap4(tahap4.orElse(new Tahap4()));
        status.setTahap4Percentage(tahap4.map(Tahap4::getCompletionPercentage).orElse(0));

        Optional<Tahap5> tahap5 = tahap5Repository.findByKegiatanId(kegiatanId);
        status.setTahap5(tahap5.orElse(new Tahap5()));
        status.setTahap5Percentage(tahap5.map(Tahap5::getCompletionPercentage).orElse(0));

        Optional<Tahap6> tahap6 = tahap6Repository.findByKegiatanId(kegiatanId);
        status.setTahap6(tahap6.orElse(new Tahap6()));
        status.setTahap6Percentage(tahap6.map(Tahap6::getCompletionPercentage).orElse(0));

        Optional<Tahap7> tahap7 = tahap7Repository.findByKegiatanId(kegiatanId);
        status.setTahap7(tahap7.orElse(new Tahap7()));
        status.setTahap7Percentage(tahap7.map(Tahap7::getCompletionPercentage).orElse(0));

        Optional<Tahap8> tahap8 = tahap8Repository.findByKegiatanId(kegiatanId);
        status.setTahap8(tahap8.orElse(new Tahap8()));
        status.setTahap8Percentage(tahap8.map(Tahap8::getCompletionPercentage).orElse(0));

        return status;
    }

    private Tahap1 createDefaultTahap1(Long kegiatanId) {
        Tahap1 tahap1 = new Tahap1();

        // Set the kegiatan reference
        Kegiatan kegiatan = new Kegiatan();
        kegiatan.setId(kegiatanId);
        tahap1.setKegiatan(kegiatan);

        // Initialize all subtahap flags to false
        tahap1.setSubtahap_1(false);
        tahap1.setSubtahap_2(false);
        tahap1.setSubtahap_3(false);
        tahap1.setSubtahap_4(false);
        tahap1.setSubtahap_5(false);
        tahap1.setSubtahap_6(false);

        // Save the new entity
        return tahap1Repository.save(tahap1);
    }

    private Tahap2 createDefaultTahap2(Long kegiatanId) {
        Tahap2 tahap2 = new Tahap2();

        // Set the kegiatan reference
        Kegiatan kegiatan = new Kegiatan();
        kegiatan.setId(kegiatanId);
        tahap2.setKegiatan(kegiatan);

        // Initialize all subtahap flags to false
        tahap2.setSubtahap_1(false);
        tahap2.setSubtahap_2(false);
        tahap2.setSubtahap_3(false);
        tahap2.setSubtahap_4(false);

        // Save the new entity
        return tahap2Repository.save(tahap2);
    }

    private Tahap3 createDefaultTahap3(Long kegiatanId) {
        Tahap3 tahap3 = new Tahap3();

        // Set the kegiatan reference
        Kegiatan kegiatan = new Kegiatan();
        kegiatan.setId(kegiatanId);
        tahap3.setKegiatan(kegiatan);

        // Initialize all subtahap flags to false
        tahap3.setSubtahap_1(false);
        tahap3.setSubtahap_2(false);
        tahap3.setSubtahap_3(false);
        tahap3.setSubtahap_4(false);

        // Save the new entity
        return tahap3Repository.save(tahap3);
    }

    private Tahap4 createDefaultTahap4(Long kegiatanId) {
        Tahap4 tahap4 = new Tahap4();

        // Set the kegiatan reference
        Kegiatan kegiatan = new Kegiatan();
        kegiatan.setId(kegiatanId);
        tahap4.setKegiatan(kegiatan);

        // Initialize all subtahap flags to false
        tahap4.setSubtahap_1(false);
        tahap4.setSubtahap_2(false);
        tahap4.setSubtahap_3(false);
        tahap4.setSubtahap_4(false);

        // Save the new entity
        return tahap4Repository.save(tahap4);
    }

    private Tahap5 createDefaultTahap5(Long kegiatanId) {
        Tahap5 tahap5 = new Tahap5();

        // Set the kegiatan reference
        Kegiatan kegiatan = new Kegiatan();
        kegiatan.setId(kegiatanId);
        tahap5.setKegiatan(kegiatan);

        // Initialize all subtahap flags to false
        tahap5.setSubtahap_1(false);
        tahap5.setSubtahap_2(false);
        tahap5.setSubtahap_3(false);
        tahap5.setSubtahap_4(false);

        // Save the new entity
        return tahap5Repository.save(tahap5);
    }

    private Tahap6 createDefaultTahap6(Long kegiatanId) {
        Tahap6 tahap6 = new Tahap6();

        // Set the kegiatan reference
        Kegiatan kegiatan = new Kegiatan();
        kegiatan.setId(kegiatanId);
        tahap6.setKegiatan(kegiatan);

        // Initialize all subtahap flags to false
        tahap6.setSubtahap_1(false);
        tahap6.setSubtahap_2(false);
        tahap6.setSubtahap_3(false);
        tahap6.setSubtahap_4(false);

        // Save the new entity
        return tahap6Repository.save(tahap6);
    }

    private Tahap7 createDefaultTahap7(Long kegiatanId) {
        Tahap7 tahap7 = new Tahap7();

        // Set the kegiatan reference
        Kegiatan kegiatan = new Kegiatan();
        kegiatan.setId(kegiatanId);
        tahap7.setKegiatan(kegiatan);

        // Initialize all subtahap flags to false
        tahap7.setSubtahap_1(false);
        tahap7.setSubtahap_2(false);
        tahap7.setSubtahap_3(false);
        tahap7.setSubtahap_4(false);

        // Save the new entity
        return tahap7Repository.save(tahap7);
    }

    private Tahap8 createDefaultTahap8(Long kegiatanId) {
        Tahap8 tahap8 = new Tahap8();

        // Set the kegiatan reference
        Kegiatan kegiatan = new Kegiatan();
        kegiatan.setId(kegiatanId);
        tahap8.setKegiatan(kegiatan);

        // Initialize all subtahap flags to false
        tahap8.setSubtahap_1(false);
        tahap8.setSubtahap_2(false);
        tahap8.setSubtahap_3(false);
        tahap8.setSubtahap_4(false);

        // Save the new entity
        return tahap8Repository.save(tahap8);
    }

    // Add method to handle file upload for specific tahap
    public void uploadFileForTahap(Long kegiatanId, int tahapId, MultipartFile file) throws IOException {
        // Validate if this tahap accepts file uploads (tahap 7 and 8)
        if (tahapId != 7 && tahapId != 8) {
            throw new IllegalArgumentException("File upload is only allowed for tahap 7 and 8");
        }

        // Store the file
        String storedFilename = fileUploadService.storeFile(file, kegiatanId, tahapId);

        // Update the tahap entity with file reference if needed
        if (tahapId == 7) {
            Tahap7 tahap7 = tahap7Repository.findByKegiatanId(kegiatanId)
                    .orElseGet(() -> createDefaultTahap7(kegiatanId));

            tahap7.setUploadFileName(file.getOriginalFilename());
            tahap7.setUploadFilePath("/uploads/kegiatan/" + kegiatanId + "/tahap/" + tahapId + "/" + storedFilename);
            tahap7.setUploadTimestamp(LocalDateTime.now());

            tahap7Repository.save(tahap7);
        } else if (tahapId == 8) {
            Tahap8 tahap8 = tahap8Repository.findByKegiatanId(kegiatanId)
                    .orElseGet(() -> createDefaultTahap8(kegiatanId));

            tahap8.setUploadFileName(file.getOriginalFilename());
            tahap8.setUploadFilePath("/uploads/kegiatan/" + kegiatanId + "/tahap/" + tahapId + "/" + storedFilename);
            tahap8.setUploadTimestamp(LocalDateTime.now());

            tahap8Repository.save(tahap8);
        }
    }

    // Add method to get uploaded files for a tahap
    public List<String> getUploadedFilesForTahap(Long kegiatanId, int tahapId) {
        return fileUploadService.getUploadedFiles(kegiatanId, tahapId);
    }
}
