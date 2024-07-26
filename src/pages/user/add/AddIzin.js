import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarUser";
import Sidebar from "../../../components/SidebarUser";
import Swal from "sweetalert2";
import axios from "axios";
import { API_DUMMY } from "../../../utils/api";

function AddIzin() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [keteranganIzin, setKeteranganIzin] = useState("");
  const [currentDateTime, setCurrentDateTime] = useState(new Date());

  const AddIzin = async (e) => {
    e.preventDefault();

    const add = {
      keteranganIzin: keteranganIzin,
    };

    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    if (!userId) {
      console.error("UserID tidak tersedia");
      return;
    }

    try {
      await axios.post(`${API_DUMMY}/api/absensi/izin/${userId}`, add, {
        headers: {
          Authorization: `${token}`,
        },
      });
      Swal.fire({
        position: "center",
        icon: "success",
        title: "Berhasil ditambahkan",
        showConfirmButton: false,
        timer: 1500,
      });
      setTimeout(() => {
        window.location.href = "/user/history_absen";
      }, 1500);
    } catch (err) {
      console.log(err);
      Swal.fire({
        position: "center",
        icon: "error",
        title: "Terjadi Kesalahan!",
        text: "Mohon coba lagi",
        showConfirmButton: false,
        timer: 1500,
      });
    }
  };

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentDateTime(new Date());
    }, 1000); // Update every second

    return () => clearInterval(interval);
  }, []); // No dependencies, so this effect runs only once when the component is mounted

  const tambahkanNolDepan = (num) => {
    return num < 10 ? "0" + num : num;
  };

  const jamSekarang = currentDateTime.getHours();

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

  const handleBack = () => {
    window.location.href = "/user/dashboard";
  };

  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar toggleSidebar={toggleSidebar} />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar isOpen={sidebarOpen} />
        </div>
        <div className="content-page max-h-screen container p-8 min-h-screen ml-0 sm:ml-64">
          <div className="add-izin mt-12 bg-white p-5 rounded-xl shadow-lg border border-gray-300">
            <h1 className="text-lg sm:text-2xl font-medium mb-4 sm:mb-7">
              Halaman Izin
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
            <form onSubmit={AddIzin}>
              <div className="relative mb-3">
                <label className="block mb-2 text-lg sm:text-sm font-medium text-gray-900">
                  Keterangan Izin
                </label>
                <textarea
                  type="text"
                  id="keterangan"
                  className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-lg sm:text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-5"
                  placeholder="Masukkan Keterangan Izin"
                  value={keteranganIzin}
                  onChange={(e) => setKeteranganIzin(e.target.value)}
                  required
                />
              </div>
              <div className="flex justify-between mt-6">
                <button
                  type="button"
                  onClick={handleBack}
                  className="block w-20 sm:w-24 rounded-lg text-black outline outline-red-500 py-3 text-sm sm:text-xs font-medium"
                >
                  Kembali
                </button>
                <button
                  type="submit"
                  className="block w-20 sm:w-24 rounded-lg text-black outline outline-blue-500 py-3 text-lg sm:text-sm font-medium"
                >
                  Simpan
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AddIzin;
