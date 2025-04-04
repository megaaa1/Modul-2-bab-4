import java.util.Scanner; // Import Scanner untuk input dari user

public class Swalayan {
    /*
     * Class Pelanggan sebagai inner class yang mengimplementasikan encapsulation
     * Semua atribut dibuat private untuk melindungi data dari akses langsung
     */
    static class Pelanggan {
        // Atribut private untuk enkapsulasi data
        private String nomorPelanggan;
        private String nama;
        private double saldo;
        private String pin; // PIN disimpan sebagai private untuk keamanan
        private int percobaanLogin; // Menghitung percobaan login gagal
        private boolean terkunci; // Status kunci akun

        /*
         * Constructor untuk inisialisasi objek Pelanggan
         * parameter nomorPelanggan - 10 digit, 2 digit pertama menentukan jenis akun
         * parameter nama - nama pelanggan
         * parameter saldo - saldo awal
         * parameter pin - PIN 4 digit untuk autentikasi
         */
        public Pelanggan(String nomorPelanggan, String nama, double saldo, String pin) {
            // Validasi nomor pelanggan
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

        /*
         * Method untuk menentukan jenis akun berdasarkan 2 digit pertama nomor pelanggan
         * return String jenis akun (Silver, Gold, Platinum)
         */
        public String getJenisAkun() {
            String prefix = nomorPelanggan.substring(0, 2);
            switch (prefix) {
                case "38": return "Silver";
                case "56": return "Gold";
                case "74": return "Platinum";
                default: return "Reguler";
            }
        }

        /*
         * Method untuk verifikasi PIN
         * parameter pinMasukan - PIN yang dimasukkan user
         * return true jika PIN benar, false jika salah
         */
        public boolean verifikasiPin(String pinMasukan) {
            if (terkunci) {
                System.out.println("Akun terkunci karena terlalu banyak percobaan salah.");
                return false;
            }
            
            if (pinMasukan.equals(pin)) {
                percobaanLogin = 0; // Reset counter percobaan jika berhasil
                return true;
            } else {
                percobaanLogin++;
                System.out.println("PIN salah. Percobaan tersisa: " + (3 - percobaanLogin));
                if (percobaanLogin >= 3) {
                    terkunci = true; // Kunci akun setelah 3x salah
                    System.out.println("Akun Anda telah terkunci!");
                }
                return false;
            }
        }

        /*
         * Method untuk top up saldo
         * parameter jumlah - jumlah yang akan di-top up
         * return true jika berhasil, false jika gagal
         */
        public boolean topUp(double jumlah) {
            if (jumlah <= 0) {
                System.out.println("Jumlah top up harus lebih dari 0");
                return false;
            }
            
            saldo += jumlah;
            System.out.printf("Top up berhasil. Saldo baru: Rp%,.2f%n", saldo);
            return true;
        }

        /*
         * Method untuk transaksi pembelian
         * parameter jumlah - jumlah pembelian
         * return true jika berhasil, false jika gagal
         */
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
            // Validasi saldo minimal Rp10.000
            if (saldoSementara < 10000) {
                System.out.println("Transaksi gagal. Saldo minimal Rp10.000 harus tersisa");
                return false;
            }

            // Hitung cashback berdasarkan jenis akun
            double cashback = hitungCashback(jumlah);
            saldo = saldoSementara + cashback;
            
            System.out.printf("Pembelian berhasil. Cashback: Rp%,.2f%n", cashback);
            System.out.printf("Saldo baru: Rp%,.2f%n", saldo);
            return true;
        }

        /*
         * Method private untuk menghitung cashback (hanya bisa diakses dari dalam class)
         * parameter jumlahBelanja - jumlah pembelian
         * return jumlah cashback yang didapat
         */
        private double hitungCashback(double jumlahBelanja) {
            String jenis = getJenisAkun();
            double cashback = 0;

            switch (jenis) {
                case "Silver":
                    if (jumlahBelanja > 1000000) {
                        cashback = jumlahBelanja * 0.05; // 5% untuk pembelian > 1jt
                    }
                    break;
                case "Gold":
                    // 7% untuk pembelian > 1jt, 2% untuk lainnya
                    cashback = jumlahBelanja * (jumlahBelanja > 1000000 ? 0.07 : 0.02);
                    break;
                case "Platinum":
                    // 10% untuk pembelian > 1jt, 5% untuk lainnya
                    cashback = jumlahBelanja * (jumlahBelanja > 1000000 ? 0.10 : 0.05);
                    break;
            }

            return cashback;
        }

        // Method untuk menampilkan informasi akun
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
        Scanner scanner = new Scanner(System.in); // Membuat objek Scanner untuk input
        
        // Inisialisasi data pelanggan
        Pelanggan[] daftarPelanggan = {
            new Pelanggan("3801234567", "Budi Santoso", 500000, "1234"),
            new Pelanggan("5609876543", "Ani Wijaya", 1500000, "4321"),
            new Pelanggan("7412345678", "Citra Dewi", 3000000, "0000")
        };
        
        Pelanggan pelangganAktif = null; // Menyimpan pelanggan yang sedang login
        
        System.out.println("=== SELAMAT DATANG DI SWALAYAN MEGA ===");
        
        // Loop utama program
        while (true) {
            if (pelangganAktif == null) {
                // Menu Login jika belum login
                System.out.println("\n=== MENU LOGIN ===");
                System.out.print("Masukkan nomor pelanggan (10 digit): ");
                String nomor = scanner.nextLine();
                
                System.out.print("Masukkan PIN: ");
                String pin = scanner.nextLine();
                
                boolean ditemukan = false;
                // Cari pelanggan berdasarkan nomor
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
                // Menu Utama setelah login
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
                
                // Proses pilihan menu
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
