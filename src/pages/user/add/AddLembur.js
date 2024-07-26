import React, { useState } from "react";
import Navbar from "../../../components/NavbarUser";
import Sidebar from "../../../components/SidebarUser";
import axios from "axios";
import Swal from "sweetalert2";
import { API_DUMMY } from "../../../utils/api";

function AddLembur() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [jam_mulai, setJamMulai] = useState("");
  const [jam_selesai, setJamSelesai] = useState("");
  const [keterangan_lembur, setKeteranganLembur] = useState("");
  const [tanggal_lembur, setTanggalLembur] = useState("");
  const [nama, setNama] = useState("");

  const addLembur = async (e) => {
    e.preventDefault();

    const add = {
      jamMulai: jam_mulai,
      jamSelesai: jam_selesai,
      keteranganLembur: keterangan_lembur,
      nama: nama,
      tanggalLembur: tanggal_lembur,
    };

    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    if (!userId) {
      // Jika userId tidak tersedia
      console.error("UserID tidak tersedia");
      return;
    }

    try {
      await axios.post(`${API_DUMMY}/api/lembur/tambahLembur/${userId}`, add, {
        headers: {
          Authorization: `Bearer ${token}`,
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
        window.location.href = "/user/history_lembur";
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
        <div className="content-page max-h-screen container p-8 min-h-screen ml-64">
          <h1 className="judul text-3xl font-semibold">Halaman Lembur</h1>
          <div className="add-lembur mt-12 bg-white p-5 rounded-xl shadow-lg border border-gray-300">
            <p className="text-lg sm:text-xl font-medium mb-4 sm:mb-7">
              Halaman Lembur
            </p>
            <form onSubmit={addLembur}>
              <div className="grid grid-cols-2 gap-4">
                <div className="relative mb-3">
                  <label className="block mb-2 text-sm sm:text-xs font-medium text-gray-900">
                    Tanggal Lembur
                  </label>
                  <input
                    type="date"
                    id="tanggallembur"
                    className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm sm:text-xs rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 "
                    placeholder="Masukkan Tanggal Lembur"
                    value={tanggal_lembur}
                    onChange={(e) => setTanggalLembur(e.target.value)}
                    required
                  />
                </div>
                <div className="relative">
                  <label className="block mb-2 text-sm sm:text-xs font-medium text-gray-900 ">
                    Keterangan Lembur
                  </label>
                  <input
                    type="text"
                    id="keterangan"
                    className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm sm:text-xs rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 "
                    placeholder="Masukkan Keterangan Lembur"
                    value={keterangan_lembur}
                    onChange={(e) => setKeteranganLembur(e.target.value)}
                    required
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="relative">
                  <label className="block mb-2 text-sm sm:text-xs font-medium text-gray-900 ">
                    Jam Mulai
                  </label>
                  <input
                    type="time"
                    id="mulai"
                    className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm sm:text-xs rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 "
                    placeholder="Masukkan Jam Mulai"
                    value={jam_mulai}
                    onChange={(e) => setJamMulai(e.target.value)}
                    required
                  />
                </div>
                <div className="relative">
                  <label className="block mb-2 text-sm sm:text-xs font-medium text-gray-900 ">
                    Jam Selesai
                  </label>
                  <input
                    type="time"
                    id="selsai"
                    className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm sm:text-xs rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 "
                    placeholder="Masukkan Jam Selesai"
                    value={jam_selesai}
                    onChange={(e) => setJamSelesai(e.target.value)}
                    required
                  />
                </div>
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
                  className="block w-20 sm:w-24 rounded-lg text-black outline outline-blue-500 py-3 text-sm sm:text-xs font-medium"
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

export default AddLembur;
