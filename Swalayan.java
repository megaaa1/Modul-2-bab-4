import java.util.Scanner;

public class Swalayan {
    // Class Pelanggan sebagai inner class
    static class Pelanggan {
        private String nomorPelanggan;
        private String nama;
        private double saldo;
        private String pin;
        private int percobaanLogin;
        private boolean terkunci;

        public Pelanggan(String nomorPelanggan, String nama, double saldo, String pin) {
            if (nomorPelanggan.length() != 10) {
                throw new IllegalArgumentException("Nomor pelanggan harus 10 digit");
            }
            this.nomorPelanggan = nomorPelanggan;
            this.nama = nama;
            this.saldo = saldo;
            this.pin = pin;
            this.percobaanLogin = 0;
            this.terkunci = false;
        }

        public String getJenisAkun() {
            String prefix = nomorPelanggan.substring(0, 2);
            switch (prefix) {
                case "38": return "Silver";
                case "56": return "Gold";
                case "74": return "Platinum";
                default: return "Reguler";
            }
        }

        public boolean verifikasiPin(String pinMasukan) {
            if (terkunci) {
                System.out.println("Akun terkunci karena terlalu banyak percobaan salah.");
                return false;
            }
            
            if (pinMasukan.equals(pin)) {
                percobaanLogin = 0;
                return true;
            } else {
                percobaanLogin++;
                System.out.println("PIN salah. Percobaan tersisa: " + (3 - percobaanLogin));
                if (percobaanLogin >= 3) {
                    terkunci = true;
                    System.out.println("Akun Anda telah terkunci!");
                }
                return false;
            }
        }

        public boolean topUp(double jumlah) {
            if (jumlah <= 0) {
                System.out.println("Jumlah top up harus lebih dari 0");
                return false;
            }
            
            saldo += jumlah;
            System.out.printf("Top up berhasil. Saldo baru: Rp%,.2f%n", saldo);
            return true;
        }

        public boolean belanja(double jumlah) {
            if (terkunci) {
                System.out.println("Akun terkunci, tidak dapat melakukan transaksi");
                return false;
            }
            if (jumlah <= 0) {
                System.out.println("Jumlah pembelian harus lebih dari 0");
                return false;
            }
            
            double saldoSementara = saldo - jumlah;
            if (saldoSementara < 10000) {
                System.out.println("Transaksi gagal. Saldo minimal Rp10.000 harus tersisa");
                return false;
            }

            double cashback = hitungCashback(jumlah);
            saldo = saldoSementara + cashback;
            
            System.out.printf("Pembelian berhasil. Cashback: Rp%,.2f%n", cashback);
            System.out.printf("Saldo baru: Rp%,.2f%n", saldo);
            return true;
        }

        private double hitungCashback(double jumlahBelanja) {
            String jenis = getJenisAkun();
            double cashback = 0;

            switch (jenis) {
                case "Silver":
                    if (jumlahBelanja > 1000000) {
                        cashback = jumlahBelanja * 0.05;
                    }
                    break;
                case "Gold":
                    cashback = jumlahBelanja * (jumlahBelanja > 1000000 ? 0.07 : 0.02);
                    break;
                case "Platinum":
                    cashback = jumlahBelanja * (jumlahBelanja > 1000000 ? 0.10 : 0.05);
                    break;
            }

            return cashback;
        }

        public void tampilkanInfoAkun() {
            System.out.println("\n=== Informasi Akun ===");
            System.out.println("Nomor Pelanggan: " + nomorPelanggan);
            System.out.println("Nama: " + nama);
            System.out.println("Jenis Akun: " + getJenisAkun());
            System.out.printf("Saldo: Rp%,.2f%n", saldo);
            System.out.println("Status: " + (terkunci ? "Terkunci" : "Aktif"));
        }
    }

    // Main program
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Inisialisasi data pelanggan
        Pelanggan[] daftarPelanggan = {
            new Pelanggan("3801234567", "Budi Santoso", 500000, "1234"),
            new Pelanggan("5609876543", "Ani Wijaya", 1500000, "4321"),
            new Pelanggan("7412345678", "Citra Dewi", 3000000, "0000")
        };
        
        Pelanggan pelangganAktif = null;
        
        System.out.println("=== SELAMAT DATANG DI SWALAYAN TINY ===");
        
        while (true) {
            if (pelangganAktif == null) {
                // Menu Login
                System.out.println("\n=== MENU LOGIN ===");
                System.out.print("Masukkan nomor pelanggan (10 digit): ");
                String nomor = scanner.nextLine();
                
                System.out.print("Masukkan PIN: ");
                String pin = scanner.nextLine();
                
                boolean ditemukan = false;
                for (Pelanggan pelanggan : daftarPelanggan) {
                    if (pelanggan.nomorPelanggan.equals(nomor)) {
                        ditemukan = true;
                        if (pelanggan.verifikasiPin(pin)) {
                            pelangganAktif = pelanggan;
                            System.out.println("Login berhasil! Selamat datang " + pelanggan.nama);
                        }
                        break;
                    }
                }
                
                if (!ditemukan) {
                    System.out.println("Nomor pelanggan tidak ditemukan.");
                }
            } else {
                // Menu Utama
                System.out.println("\n=== MENU UTAMA ===");
                System.out.println("1. Lihat Informasi Akun");
                System.out.println("2. Top Up Saldo");
                System.out.println("3. Transaksi Pembelian");
                System.out.println("4. Logout");
                System.out.print("Pilih menu (1-4): ");
                
                int pilihan;
                try {
                    pilihan = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Masukkan angka antara 1-4");
                    continue;
                }
                
                switch (pilihan) {
                    case 1:
                        pelangganAktif.tampilkanInfoAkun();
                        break;
                    case 2:
                        System.out.print("\nMasukkan jumlah top up: Rp");
                        try {
                            double jumlah = Double.parseDouble(scanner.nextLine());
                            pelangganAktif.topUp(jumlah);
                        } catch (NumberFormatException e) {
                            System.out.println("Masukkan jumlah yang valid");
                        }
                        break;
                    case 3:
                        System.out.print("\nMasukkan jumlah pembelian: Rp");
                        try {
                            double jumlah = Double.parseDouble(scanner.nextLine());
                            pelangganAktif.belanja(jumlah);
                        } catch (NumberFormatException e) {
                            System.out.println("Masukkan jumlah yang valid");
                        }
                        break;
                    case 4:
                        pelangganAktif = null;
                        System.out.println("Logout berhasil.");
                        break;
                    default:
                        System.out.println("Pilihan tidak valid. Silakan pilih 1-4");
                }
            }
        }
    }
}