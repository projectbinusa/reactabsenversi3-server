import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarAdmin";
import Sidebar from "../../../components/SidebarUser";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faFileExport,
  faInfo,
  faMagnifyingGlass,
  faSearch,
} from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import { Pagination } from "flowbite-react";
import { API_DUMMY } from "../../../utils/api";
import Swal from "sweetalert2";

function Absensi() {
  const [absensi, setAbsensi] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [limit, setLimit] = useState(5);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  const getAllAbsensi = async () => {
    const token = localStorage.getItem("token");
    const adminId = localStorage.getItem("adminId");

    try {
      const response = await axios.get(
        `${API_DUMMY}/api/absensi/admin/${adminId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setAbsensi(response.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  useEffect(() => {
    getAllAbsensi();
  }, []);

  const formatDate = (dateString) => {
    const options = {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    };
    return new Date(dateString).toLocaleDateString("id-ID", options);
  };

  useEffect(() => {
    const filteredData = absensi.filter(
      (absensi) =>
        absensi.user?.username
          ?.toLowerCase()
          .includes(searchTerm.toLowerCase()) ||
        absensi.statusAbsen?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (formatDate(absensi.tanggalAbsen)
          ?.toLowerCase()
          .includes(searchTerm.toLowerCase()) ??
          false)
    );
    setTotalPages(Math.ceil(filteredData.length / limit));
  }, [searchTerm, limit, absensi]);

  const handleSearch = (event) => {
    setSearchTerm(event.target.value);
  };

  const handleLimitChange = (event) => {
    setLimit(parseInt(event.target.value));
    setCurrentPage(1); // Reset to the first page when limit changes
  };

  function onPageChange(page) {
    setCurrentPage(page);
  }

  const filteredAbsensi = absensi.filter(
    (absensi) =>
      absensi.user?.username
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase()) ||
      absensi.statusAbsen?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (formatDate(absensi.tanggalAbsen)
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase()) ??
        false)
  );

  const paginatedAbsensi = filteredAbsensi.slice(
    (currentPage - 1) * limit,
    currentPage * limit
  );

  const exportAbsensi = async () => {
    const token = localStorage.getItem("token");

    try {
      const response = await axios.get(
        `http://localhost:2024/api/absensi/rekap-perkaryawan/export`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          responseType: "blob", // Important for handling binary data
        }
      );

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `absensi.xlsx`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);

      Swal.fire({
        title: "Berhasil",
        text: "Berhasil mengunduh data",
        icon: "success",
        showConfirmButton: false,
        timer: 1500, // Auto close after 1.5 seconds
      });
      window.location.reload();
    } catch (error) {
      Swal.fire({
        title: "Error",
        text: "Gagal mengunduh data",
        icon: "error",
        showConfirmButton: false,
        timer: 1500, // Auto close after 1.5 seconds
      });
      console.log(error);
    }
  };

  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
        <div className="sm:ml-64 content-page container ml-0 md:ml-64 mt-6 text-center">
          <div className="p-5 mt-5">
            <main id="content" className="flex-1 p-4 sm:p-6">
              <div className="bg-white rounded-lg shadow-xl p-8">
                <div className="flex justify-between">
                  <h6 className="text-xl font-bold">Detail History Absensi</h6>
                  <div className="flex items-center gap-2 mt-2">
                    <div className="relative w-64">
                      <input
                        type="search"
                        id="search-dropdown"
                        value={searchTerm}
                        onChange={handleSearch}
                        className="block p-2.5 w-full z-20 text-sm rounded-l-md text-gray-900 bg-gray-50 border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:border-blue-500"
                        placeholder="Search name..."
                        required
                      />
                    </div>
                    <select
                      value={limit}
                      onChange={handleLimitChange}
                      className="flex-shrink-0 z-10 inline-flex rounded-r-md items-center py-2.5 px-4 text-sm font-medium text-gray-900 bg-gray-100 border border-gray-300 hover:bg-gray-200 focus:ring-4 focus:outline-none focus:ring-gray-100 dark:bg-gray-700 dark:hover:bg-gray-600 dark:focus:ring-gray-700 dark:text-white dark:border-gray-600"
                    >
                      <option value="5">05</option>
                      <option value="10">10</option>
                      <option value="20">20</option>
                      <option value="50">50</option>
                    </select>
                    {/* <button
                      type="submit"
                      className="bg-indigo-500 hover:bg-indigo text-white font-bold py-2 px-4 rounded inline-block"
                    >
                      <FontAwesomeIcon icon={faMagnifyingGlass} />
                    </button> */}
                    <a
                      onClick={exportAbsensi}
                      className="exp bg-green-500 hover:bg-green text-white font-bold py-2 px-4 rounded inline-block ml-auto"
                    >
                      <FontAwesomeIcon icon={faFileExport} />
                    </a>
                  </div>
                </div>
                <hr className="mt-3" />
                <form
                  action=""
                  method="post"
                  className="flex flex-col sm:flex-row justify-center items-center gap-4 mt-5"
                >
                  {/* <select
                    className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                    id="bulan"
                    name="bulan"
                  >
                    <option>Pilih Bulan</option>
                    <option value="01">Januari</option>
                    <option value="02">Februari</option>
                    <option value="03">Maret</option>
                    <option value="04">April</option>
                    <option value="05">Mei</option>
                    <option value="06">Juni</option>
                    <option value="07">Juli</option>
                    <option value="08">Agustus</option>
                    <option value="09">September</option>
                    <option value="10">Oktober</option>
                    <option value="11">November</option>
                    <option value="12">Desember</option>
                  </select> */}
                  {/* <input
                    type="text"
                    className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                    id="tanggal"
                    name="tanggal"
                    placeholder="Pilih Tanggal"
                    value=""
                  />
                  <input
                    type="number"
                    className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                    id="tahun"
                    name="tahun"
                    placeholder="Pilih Tahun"
                    value=""
                  /> */}
                </form>

                <div className="relative overflow-x-auto shadow-md sm:rounded-lg mt-5">
                  <table
                    id="rekapSimple"
                    className="w-full text-sm text-left text-gray-500 dark:text-gray-400"
                  >
                    <thead className="text-left text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                      <tr>
                        <th scope="col" className="px-4 py-3">
                          No
                        </th>
                        <th scope="col" className="px-4 py-3">
                          Username
                        </th>
                        <th scope="col" className="px-4 py-3">
                          Tanggal
                        </th>
                        <th scope="col" className="px-4 py-3">
                          Kehadiran
                        </th>
                        <th scope="col" className="px-4 py-3">
                          Jam Masuk
                        </th>
                        <th scope="col" className="px-4 py-3">
                          Foto Masuk
                        </th>
                        <th scope="col" className="px-4 py-3">
                          Jam Pulang
                        </th>
                        <th scope="col" className="px-4 py-3">
                          Foto Pulang
                        </th>
                        <th scope="col" className="px-4 py-3">
                          Jam Kerja
                        </th>
                        <th scope="col" className="px-4 py-3">
                          Aksi
                        </th>
                      </tr>
                    </thead>
                    <tbody className="text-left">
                      {paginatedAbsensi.map((absensi, index) => (
                        <tr
                          key={index}
                          className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600"
                        >
                          <th
                            scope="row"
                            className="px-4 py-4 font-medium text-gray-900 dark:text-white"
                          >
                            {(currentPage - 1) * limit + index + 1}
                          </th>
                          <td className="px-4 py-2 text-gray-700 text-center capitalize">
                            {absensi.user.username}
                          </td>
                          <td className="px-4 py-2 text-gray-700 text-center capitalize">
                            {formatDate(absensi.tanggalAbsen)}
                          </td>
                          <td className="px-4 py-2 text-gray-700 text-center capitalize">
                            {absensi.statusAbsen}
                          </td>
                          <td className="px-4 py-2 text-gray-700 text-center capitalize">
                            {absensi.jamMasuk}
                          </td>
                          <td className="px-4 py-2 text-gray-700 text-center capitalize">
                            <img
                              src={absensi.fotoMasuk ? absensi.fotoMasuk : "-"}
                              alt="Foto Masuk"
                              className="block py-2.5 px-0 w-25 max-h-32 h-25 text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                              id="foto_masuk"
                            />
                          </td>
                          <td className="px-4 py-4">{absensi.jamPulang}</td>
                          <td className="px-4 py-4">
                            <img
                              src={
                                absensi.fotoPulang ? absensi.fotoPulang : "-"
                              }
                              alt="Foto Pulang"
                              className="block py-2.5 px-0 w-25 max-h-96 h-25 text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                              id="foto_masuk"
                            />
                          </td>
                          <td className="px-4 py-2 text-gray-700 text-center capitalize">
                            00 jam 00 menit
                          </td>
                          <td className="px-4 py-2 text-gray-700 text-center capitalize">
                            <a href={`/admin/detailA/${absensi.id}`}>
                              <button className="z-20 block rounded-full border-2 border-white bg-blue-100 p-4 text-blue-700 active:bg-blue-50">
                                <span className="relative inline-block">
                                  <FontAwesomeIcon
                                    icon={faInfo}
                                    className="h-4 w-4"
                                  />
                                </span>
                              </button>
                            </a>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
                <Pagination
                  className="mt-5"
                  layout="table"
                  currentPage={currentPage}
                  totalPages={totalPages}
                  onPageChange={onPageChange}
                  showIcons
                />
              </div>
            </main>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Absensi;
