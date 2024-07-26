import React, { useState } from "react";
import Navbar from "../../../components/NavbarUser";
import Sidebar from "../../../components/SidebarUser";
import Swal from "sweetalert2";
import axios from "axios";
import { API_DUMMY } from "../../../utils/api";

function AddCuti() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [awal_cuti, setAwalCuti] = useState("");
  const [akhir_cuti, setAkhirCuti] = useState("");
  const [masuk_kerja, setMasukKerja] = useState("");
  const [keperluan, setKeperluan] = useState("");

  const addCuti = async (e) => {
    e.preventDefault();

    const add = {
      awalCuti: awal_cuti,
      akhirCuti: akhir_cuti,
      masukKerja: masuk_kerja,
      keperluan: keperluan,
    };

    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    if (!userId) {
      // Jika userId tidak tersedia
      console.error("UserID tidak tersedia");
      return;
    }

    try {
      await axios.post(`${API_DUMMY}/api/cuti/tambahCuti/${userId}`, add, {
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
        window.location.href = "/user/history_cuti";
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
          <div className="add-cuti mt-12 bg-white p-5 rounded-xl shadow-lg border border-gray-300">
            <h1 className="text-lg sm:text-xl font-medium mb-4 sm:mb-7">
              Halaman Cuti
            </h1>
            <form onSubmit={addCuti}>
              <div className="md:grid grid-cols-2 gap-4">
                <div className="relative mb-3">
                  <label className="block mb-2 text-sm sm:text-xs font-medium text-gray-900">
                    Awal Cuti
                  </label>
                  <input
                    type="date"
                    id="awalcuti"
                    className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm sm:text-xs rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 "
                    placeholder="Masukkan Tanggal Awal Cuti"
                    value={awal_cuti}
                    onChange={(e) => setAwalCuti(e.target.value)}
                    required
                  />
                </div>
                <div className="relative">
                  <label className="block mb-2 text-sm sm:text-xs font-medium text-gray-900 ">
                    Akhir Cuti
                  </label>
                  <input
                    type="date"
                    id="akhircuti"
                    className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm sm:text-xs rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 "
                    placeholder="Masukkan Tanggal Akhir Cuti"
                    value={akhir_cuti}
                    onChange={(e) => setAkhirCuti(e.target.value)}
                    required
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="relative">
                  <label className="block mb-2 text-sm sm:text-xs font-medium text-gray-900 ">
                    Masuk Kerja
                  </label>
                  <input
                    type="date"
                    id="masuk"
                    className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm sm:text-xs rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 "
                    placeholder="Masukkan Tanggal Masuk Kerja"
                    value={masuk_kerja}
                    onChange={(e) => setMasukKerja(e.target.value)}
                    required
                  />
                </div>{" "}
                <div className="relative">
                  <label className="block mb-2 text-sm sm:text-xs font-medium text-gray-900 ">
                    Keperluan
                  </label>
                  <input
                    type="text"
                    id="keperluan"
                    className="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm sm:text-xs rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 "
                    placeholder="Masukkan Keperluan"
                    value={keperluan}
                    onChange={(e) => setKeperluan(e.target.value)}
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

export default AddCuti;
