import React, { useEffect, useState, useRef } from "react";
import Sidebar from "../../../components/SidebarUser";
import Navbar from "../../../components/NavbarUser";
import Webcam from "react-webcam";
import axios from "axios";
import Swal from "sweetalert2";
import { toBeDisabled } from "@testing-library/jest-dom/matchers";
import { API_DUMMY } from "../../../utils/api";

function AbsenPulang() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [currentDateTime, setCurrentDateTime] = useState(new Date());
  const webcamRef = useRef(null);
  const [error, setError] = useState("");
  const userId = localStorage.getItem("userId");
  const [loading, setLoading] = useState(false);
  const [address, setAddress] = useState("");
  const [fetchingLocation, setFetchingLocation] = useState(true);
  const [keteranganPulangAwal, setKeteranganPulangAwal] = useState("");
  const [waktuPulang, setWaktuPulang] = useState("");

  const getShift = async () => {
    try {
      const response = await axios.get(
        `${API_DUMMY}/api/shift/getShift-byUserId/${userId}`
      );

      if (response.data && response.data.waktuPulang) {
        setWaktuPulang(response.data.waktuPulang);
      } else {
        console.error(
          "Data shift tidak ditemukan atau tidak memiliki properti waktuPulang."
        );
      }
    } catch (error) {
      console.error("Error saat mengambil data shift:", error);
    }
  };

  useEffect(() => {
    getShift();
    const interval = setInterval(() => {
      setCurrentDateTime(new Date());
    }, 1000); // Perbarui setiap detik

    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    if (!fetchingLocation) {
      return;
    }

    navigator.geolocation.getCurrentPosition(async (position) => {
      const latitude = position.coords.latitude;
      const longitude = position.coords.longitude;

      try {
        const response = await fetch(
          `https://nominatim.openstreetmap.org/reverse?format=json&lat=${latitude}&lon=${longitude}&zoom=18&addressdetails=1`
        );
        const data = await response.json();
        const address = data.display_name;
        setAddress(address);
      } catch (error) {
        console.error("Error:", error);
        setError("Gagal mendapatkan alamat");
      }

      setFetchingLocation(false);
    });
  }, [fetchingLocation]);

  // Fungsi untuk menambahkan nol di depan angka jika angka kurang dari 10
  const tambahkanNolDepan = (num) => {
    return num < 10 ? "0" + num : num;
  };

  // Dapatkan jam saat ini untuk menentukan waktu hari
  const jamSekarang = currentDateTime.getHours();

  // Tentukan ucapan berdasarkan waktu hari
  let ucapan;
  if (jamSekarang < 10) {
    ucapan = "Selamat Pagi";
  } else if (jamSekarang < 15) {
    ucapan = "Selamat Siang";
  } else if (jamSekarang < 18) {
    ucapan = "Selamat Sore";
  } else {
    ucapan = "Selamat Malam";
  }

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  const handleCaptureAndSubmitPulang = async () => {
    const imageSrc = webcamRef.current.getScreenshot();
    const imageBlob = await fetch(imageSrc).then((res) => res.blob());

    setFetchingLocation(true);
    navigator.geolocation.getCurrentPosition(async (position) => {
      const latitude = position.coords.latitude;
      const longitude = position.coords.longitude;

      try {
        const absensiCheckResponse = await axios.get(
          `${API_DUMMY}/api/absensi/checkAbsensi/${userId}`
        );
        const isUserAlreadyAbsenToday =
          absensiCheckResponse.data ===
          "Pengguna sudah melakukan absensi hari ini.";

        const currentTime = new Date();
        const currentHours = currentTime.getHours();
        const currentMinutes = currentTime.getMinutes();

        if (isUserAlreadyAbsenToday) {
          // Check if there is a value in keteranganPulangAwal
          if (keteranganPulangAwal) {
            const formData = new FormData();
            formData.append("image", imageBlob);
            formData.append("lokasiPulang", address);
            formData.append("keteranganPulangAwal", keteranganPulangAwal);

            const response = await axios.put(
              `${API_DUMMY}/api/absensi/pulang/${userId}`,
              formData,
              {
                headers: {
                  "Content-Type": "multipart/form-data",
                },
              }
            );

            Swal.fire({
              position: "center",
              icon: "success",
              title: "Berhasil Pulang",
              showConfirmButton: false,
              timer: 1500,
            });
            setTimeout(() => {
              window.location.href = "/user/history_absen";
            }, 1500);
          } else if (
            currentHours > 14 ||
            (currentHours === 14 && currentMinutes >= 30)
          ) {
            const formData = new FormData();
            formData.append("image", imageBlob);
            formData.append("lokasiPulang", address);
            formData.append("keteranganPulangAwal", keteranganPulangAwal);

            const response = await axios.put(
              `${API_DUMMY}/api/absensi/pulang/${userId}`,
              formData,
              {
                headers: {
                  "Content-Type": "multipart/form-data",
                },
              }
            );

            Swal.fire({
              position: "center",
              icon: "success",
              title: "Berhasil Pulang",
              showConfirmButton: false,
              timer: 1500,
            });
            setTimeout(() => {
              window.location.href = "/user/history_absen";
            }, 1500);
          } else {
            Swal.fire(
              "Info",
              `Anda belum dapat melakukan absensi pulang sebelum pukul ${waktuPulang} `,
              "info"
            );
          }
        } else {
          Swal.fire(
            "Info",
            "Anda belum melakukan absensi masuk hari ini.",
            "info"
          );
        }
      } catch (err) {
        console.error("Error:", err);
        Swal.fire("Error", "Gagal Absen", "error");
      }

      setFetchingLocation(false);
    });
  };

  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar toggleSidebar={toggleSidebar} />
      </div>
      <div className="flex h-full">
        <div className={`fixed ${sidebarOpen ? "" : "lg:flex"}`}>
          <Sidebar isOpen={sidebarOpen} />
        </div>
        <div className="content-page max-h-screen container p-8 min-h-screen ml-0 lg:ml-64">
          <div className="add-izin mt-12 bg-white p-5 rounded-xl shadow-lg border border-gray-300">
            <h1 className="text-lg sm:text-2xl font-medium mb-4 sm:mb-7">
              Absen Pulang
            </h1>
            <div className="text-base text-center mt-2">
              {currentDateTime.toLocaleDateString("id-ID", {
                weekday: "long",
                day: "numeric",
                month: "long",
                year: "numeric",
              })}{" "}
              -{" "}
              {tambahkanNolDepan(currentDateTime.getHours()) +
                ":" +
                tambahkanNolDepan(currentDateTime.getMinutes()) +
                ":" +
                tambahkanNolDepan(currentDateTime.getSeconds())}
            </div>
            <div className="text-base text-center mt-2">{ucapan}</div>
            <form onSubmit={""}>
              <p className="font-bold text-center mt-8">Foto:</p>
              <div className="flex justify-center">
                <Webcam audio={false} ref={webcamRef} />
              </div>
              <div className="flex justify-center mt-6">
                {fetchingLocation ? (
                  <p>Mendapatkan lokasi...</p>
                ) : (
                  <p id="address">Alamat: {address}</p>
                )}
              </div>

              <div className="flex justify-center mt-6">
                <button
                  type="button"
                  onClick={() => {
                    if (!fetchingLocation) {
                      handleCaptureAndSubmitPulang();
                    } else {
                      Swal.fire(
                        "Tunggu Sebentar",
                        "Sedang mendapatakan lokasi",
                        "info"
                      );
                    }
                  }}
                  className="block w-32 sm:w-40 bg-blue-500 text-white rounded-lg py-3 text-sm sm:text-xs font-medium"
                >
                  Ambil Foto
                </button>
              </div>
              <div className="relative mb-3 mt-5">
                <input
                  type="text"
                  id="keterangan"
                  className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm sm:text-xs rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-5"
                  placeholder="Keterangan Pulang Awal"
                  value={keteranganPulangAwal}
                  onChange={(e) => setKeteranganPulangAwal(e.target.value)}
                  required
                />
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AbsenPulang;
