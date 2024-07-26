import React, { useState, useEffect } from "react";
import Select from "react-select";
import Navbar from "../../../components/NavbarAdmin";
import Sidebar from "../../../components/SidebarUser";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faFileExport,
  faMagnifyingGlass,
} from "@fortawesome/free-solid-svg-icons";
import Swal from "sweetalert2";
import axios from "axios";
import { API_DUMMY } from "../../../utils/api";
import NavbarAdmin from "../../../components/NavbarAdmin";

function Perkaryawan() {
  const [listAbsensi, setListAbsensi] = useState([]);
  const [listUser, setListUser] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const idAdmin = localStorage.getItem("adminId");

  // Fetch user data
  const getAllUserByAdmin = async () => {
    try {
      const usList = await axios.get(`${API_DUMMY}/api/user/${idAdmin}/users`);
      const userOptions = usList.data.map((user) => ({
        value: user.id,
        label: user.username
          .split(" ")
          .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
          .join(" "),
      }));
      setListUser(userOptions);
    } catch (error) {
      console.log(error);
    }
  };

  // Fetch absensi data by user id
  const getAbsensiByUserId = async (userId) => {
    try {
      const abs = await axios.get(
        `${API_DUMMY}/api/absensi/getByUserId/${userId}`
      );
      if (abs.data.length === 0) {
        Swal.fire("Gagal", "User belum pernah absensi", "error");
        setListAbsensi([]);
      } else {
        setListAbsensi(abs.data);
      }
    } catch (error) {
      console.log(error);
      Swal.fire("Gagal", "Gagal Mengambil data ", "error");
    }
  };

  // Handle user selection
  const handleUserChange = (selectedOption) => {
    setSelectedUser(selectedOption);
    if (selectedOption) {
      getAbsensiByUserId(selectedOption.value);
    } else {
      setListAbsensi([]);
    }
  };

  // Export data function
  const exportPerkaryawan = async () => {
    if (!selectedUser) {
      Swal.fire(
        "Peringatan",
        "Silakan pilih karyawan terlebih dahulu",
        "warning"
      );
      return;
    }
    try {
      const response = await axios.get(
        `${API_DUMMY}/api/absensi/export/absensi-rekapan-perkaryawan?userId=${selectedUser.value}`,
        {
          responseType: "blob",
        }
      );

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "RekapPerkawryawan.xlsx");
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);

      Swal.fire("Berhasil", "Berhasil mengunduh data", "success");
    } catch (error) {
      Swal.fire("Error", "Gagal mengunduh data", "error");
      console.log(error);
    }
  };

  // Initialize data on component mount
  useEffect(() => {
    getAllUserByAdmin();
  }, []);

  // Format date
  const formatDate = (dateString) => {
    const options = {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    };
    return new Date(dateString).toLocaleDateString("id-ID", options);
  };

  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <NavbarAdmin />
      </div>
      <div className="flex h-full pt-5">
        <div className="fixed h-full">
          <Sidebar />
        </div>
        <div className="content-page flex-1 p-8 md:ml-64 mt-16 text-center overflow-auto">
          <div className="tabel-absen bg-white p-5 rounded-xl shadow-xl border border-gray-300">
            <div className="flex justify-between">
              <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                Rekap Perkaryawan
              </h6>
            </div>
            <hr />
            <form className="flex flex-col sm:flex-row justify-center items-center gap-4 mt-5">
              <div className="w-full">
                <Select
                  options={listUser}
                  value={selectedUser}
                  onChange={handleUserChange}
                  placeholder="Pilih Karyawan"
                  className="basic-single w-full"
                  classNamePrefix="select"
                  styles={{
                    container: (provided) => ({
                      ...provided,
                      width: "100%", // Ensure it takes full width of parent container
                    }),
                    placeholder: (provided) => ({
                      ...provided,
                      textAlign: "left", // Align placeholder text to the left
                    }),
                    singleValue: (provided) => ({
                      ...provided,
                      textAlign: "left", // Align selected value text to the left
                    }),
                    menu: (provided) => ({
                      ...provided,
                      textAlign: "left", // Align options text to the left
                    }),
                    option: (provided) => ({
                      ...provided,
                      textAlign: "left", // Align individual option text to the left
                    }),
                  }}
                />
              </div>

              <div className="flex sm:flex-row gap-4 mx-auto items-center">
                <button
                  type="button"
                  className="bg-indigo-500 hover:bg-indigo text-white font-bold py-2 px-4 rounded inline-block"
                  onClick={() => getAbsensiByUserId(selectedUser?.value)}
                >
                  <FontAwesomeIcon icon={faMagnifyingGlass} />
                </button>
                <button
                  className="exp bg-green-500 hover:bg-green text-white font-bold py-2 px-4 rounded inline-block ml-auto"
                  onClick={exportPerkaryawan}
                >
                  <FontAwesomeIcon icon={faFileExport} />
                </button>
              </div>
            </form>

            <div className="relative overflow-x-auto shadow-md sm:rounded-lg mt-5">
              <table className="w-full text-sm text-left text-gray-500 dark:text-gray-400">
                <thead className="text-xs text-left text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                  <tr>
                    <th scope="col" className="px-6 py-3 whitespace-nowrap">
                      No
                    </th>
                    <th scope="col" className="px-6 py-3 whitespace-nowrap">
                      Nama
                    </th>
                    <th scope="col" className="px-6 py-3 whitespace-nowrap">
                      Tanggal
                    </th>
                    <th scope="col" className="px-6 py-3 whitespace-nowrap">
                      Jam Masuk
                    </th>
                    <th scope="col" className="px-6 py-3 whitespace-nowrap">
                      Foto Masuk
                    </th>
                    <th scope="col" className="px-6 py-3 whitespace-nowrap">
                      Jam Pulang
                    </th>
                    <th scope="col" className="px-6 py-3 whitespace-nowrap">
                      Foto Pulang
                    </th>
                    <th scope="col" className="px-6 py-3 whitespace-nowrap">
                      Jam Kerja
                    </th>
                    <th scope="col" className="px-6 py-3 whitespace-nowrap">
                      Keterangan
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {listAbsensi.map((absensi, index) => (
                    <tr key={absensi.id}>
                      <td className="px-6 py-3 whitespace-nowrap">
                        {index + 1}
                      </td>
                      <td className="px-6 py-3 whitespace-nowrap capitalize">
                        {absensi.user.username}
                      </td>
                      <td className="px-6 py-3 whitespace-nowrap capitalize">
                        {formatDate(absensi.tanggalAbsen)}
                      </td>
                      <td className="px-6 py-3 whitespace-nowrap capitalize">
                        {absensi.jamMasuk}
                      </td>
                      <td className="px-6 py-3 whitespace-nowrap capitalize">
                        <img src={absensi.fotoMasuk} alt="Foto Masuk" />
                      </td>
                      <td className="px-6 py-3 whitespace-nowrap capitalize">
                        {absensi.jamPulang}
                      </td>
                      <td className="px-6 py-3 whitespace-nowrap capitalize">
                        <img src={absensi.fotoPulang} alt="Foto Pulang" />
                      </td>
                      <td className="px-6 py-3 whitespace-nowrap capitalize">
                        {absensi.jamKerja}
                      </td>
                      <td className="px-6 py-3 whitespace-nowrap capitalize">
                        {absensi.keterangan}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Perkaryawan;
